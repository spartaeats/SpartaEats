package com.sparta.sparta_eats.cart.application.service;

import com.sparta.sparta_eats.cart.application.dto.CartSnapshot;
import com.sparta.sparta_eats.cart.application.dto.CreateCartCommand;
import com.sparta.sparta_eats.cart.application.dto.CreateCartResult;
import com.sparta.sparta_eats.cart.application.exception.CartItemNotFoundException;
import com.sparta.sparta_eats.cart.application.exception.CartNotFoundException;
import com.sparta.sparta_eats.cart.application.exception.ForbiddenCartAccessException;
import com.sparta.sparta_eats.cart.application.exception.InvalidQuantityException;
import com.sparta.sparta_eats.cart.application.exception.StoreMismatchException;
import com.sparta.sparta_eats.cart.domain.entity.Cart;
import com.sparta.sparta_eats.cart.domain.entity.CartItem;
import com.sparta.sparta_eats.cart.domain.entity.CartItemOption;
import com.sparta.sparta_eats.cart.infrastructure.repository.CartItemOptionRepository;
import com.sparta.sparta_eats.cart.infrastructure.repository.CartItemRepository;
import com.sparta.sparta_eats.cart.infrastructure.repository.CartRepository;
import com.sparta.sparta_eats.store.entity.Store;
import com.sparta.sparta_eats.store.domain.repository.StoreRepository;
import com.sparta.sparta_eats.user.infrastructure.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemOptionRepository cartItemOptionRepository;

    public CartServiceImpl(UserRepository userRepository,
                           StoreRepository storeRepository,
                           CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           CartItemOptionRepository cartItemOptionRepository) {
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartItemOptionRepository = cartItemOptionRepository;
    }


    /**
     * 장바구니 생성/재사용/교체
     * @param userId
     * @param storeId
     * @param forceReplace
     * @param items
     * @return
     */

    @Override
    public CreateCartResult createOrGetCart(String userId,
                                            UUID storeId,
                                            boolean forceReplace,
                                            List<CreateCartCommand.Item> items) {

        // --- 0) 유효성(수량 ≥ 1) ---
        if (items != null) {
            for (CreateCartCommand.Item it : items) {
                if (it.quantity() < 1) throw new IllegalArgumentException("quantity must be >= 1");
                if (it.options() != null) {
                    for (CreateCartCommand.Option op : it.options()) {
                        if (op.quantity() < 1) throw new IllegalArgumentException("option quantity must be >= 1");
                    }
                }
            }
        }

        // --- 1) 사용자/매장 로딩 ---
        userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Store store = storeRepository.findById(storeId.toString())
                .orElseThrow(() -> new IllegalArgumentException("Store not found: " + storeId));

        boolean created = false;
        boolean reused  = false;
        boolean replaced = false;

        // --- 2) 사용자 현재 카트 조회 ---
        Cart cart = cartRepository.findByUserId(userId).orElse(null);

        // --- 3) 다른 매장 카트면 교체 여부 판단 ---
        if (cart != null && !Objects.equals(cart.getStoreId(), UUID.fromString(store.getId()))) {
            if (!forceReplace) {
                throw new StoreMismatchException(cart.getStoreId(), UUID.fromString(store.getId())); // 컨트롤러에서 409 매핑
            }
            cartRepository.delete(cart); // CASCADE 로 하위 모두 삭제
            cartRepository.flush();
            cart = null;
            replaced = true;
        }

        // --- 4) 카트가 없으면 Builder로 생성 ---
        if (cart == null) {
            cart = Cart.builder()
                    .userId(userId)             // userId 필드 사용
                    .storeId(UUID.fromString(store.getId()))     // 매장은 storeId(UUID)만 보관
                    .build();
            cart = cartRepository.save(cart);
            created = true;
        } else {
            reused = true;
        }

        // --- 5) 초기 아이템 병합(merge) ---
        if (items != null && !items.isEmpty()) {
            for (CreateCartCommand.Item reqItem : items) {
                String hash = buildOptionComboHash(reqItem.itemId(), reqItem.options());

                // 5-1) 동일 조합 라인 있는지 확인
                CartItem line = cartItemRepository
                        .findByCartIdAndOptionComboHash(cart.getId(), hash)
                        .orElse(null);

                if (line == null) {
                    // 새 라인: Builder 사용
                    CartItem newLine = CartItem.builder()
                            .cart(cart)
                            .itemId(reqItem.itemId())
                            .quantity(reqItem.quantity())
                            .optionComboHash(hash)
                            .build();
                    CartItem savedLine = cartItemRepository.save(newLine);

                    // 옵션들 저장 (있다면)
                    if (reqItem.options() != null && !reqItem.options().isEmpty()) {
                        List<CartItemOption> opts = reqItem.options().stream()
                                .map(op -> CartItemOption.builder()
                                        .cartItem(savedLine)
                                        .itemOptId(op.itemOptionId())
                                        .quantity(op.quantity())
                                        .build())
                                .toList();
                        cartItemOptionRepository.saveAll(opts);
                    }
                } else {
                    // 동일 조합이면 수량만 증가
                    line.increaseQuantity(reqItem.quantity());
                }

                // 5-2) UNIQUE(cart_id, option_combo_hash) 충돌 대비 (동시 클릭 등)
                try {
                    cartItemRepository.flush();
                } catch (DataIntegrityViolationException e) {
                    CartItem existing = cartItemRepository
                            .findByCartIdAndOptionComboHash(cart.getId(), hash)
                            .orElseThrow();
                    existing.increaseQuantity(reqItem.quantity());
                    cartItemRepository.flush();
                }
            }
        }

        // --- 6) 최신 스냅샷 반환 ---
        CartSnapshot snapshot = toSnapshot(cart); // cart는 영속 상태이며 items LAZY면 필요 시 fetch 전략 조정
        return new CreateCartResult(snapshot, created, reused, replaced);
    }

    @Override
    @Transactional(readOnly = true)
    public CartSnapshot getCart(String userId, UUID cartId) {
        Cart cart = cartRepository.findWithItemsByIdAndUserId(cartId, userId)
                .orElseThrow(CartNotFoundException::new);
        return toSnapshot(cart);
    }

    @Override
    public void deleteCart(String userId, UUID cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(CartNotFoundException::new);
        if (!Objects.equals(cart.getUserId(), userId)) {
            throw new ForbiddenCartAccessException();
        }
        cartRepository.delete(cart); // CASCADE
    }

    // ================== 내부 유틸 ==================

    private CartSnapshot toSnapshot(Cart cart) {
        // cart.getItems() 가 LAZY라면 Controller에서 EntityGraph로 불러오거나
        // CartRepository의 findWithItems... 를 통해 항상 items/option을 로딩하도록 사용하세요.
        List<CartSnapshot.CartItemSnapshot> itemSnaps = cart.getItems().stream()
                .map(ci -> new CartSnapshot.CartItemSnapshot(
                        ci.getId(),
                        ci.getItemId(),
                        ci.getQuantity(),
                        ci.getOptionComboHash(),
                        ci.getOptions().stream()
                                .map(op -> new CartSnapshot.CartItemOptionSnapshot(
                                        op.getId(), op.getItemOptId(), op.getQuantity()
                                ))
                                .toList(),
                        asInstant(ci.getCreatedAt()),
                        asInstant(ci.getUpdatedAt())
                ))
                .toList();

        return new CartSnapshot(
                cart.getId(),
                cart.getUserId(),
                cart.getStoreId(),
                itemSnaps,
                asInstant(cart.getCreatedAt()),
                asInstant(cart.getUpdatedAt())
        );
    }

    private Instant asInstant(Object ts) {
        if (ts == null) return null;
        if (ts instanceof Instant i) return i;
        if (ts instanceof java.util.Date d) return d.toInstant();
        if (ts instanceof java.time.LocalDateTime ldt) return ldt.atZone(java.time.ZoneId.systemDefault()).toInstant();
        // 필요 시 다른 타입도 추가
        throw new IllegalArgumentException("Unsupported timestamp type: " + ts.getClass());
    }

    /**
     * optionComboHash 규칙:
     *  - 옵션을 itemOptionId 오름차순으로 정렬
     *  - 포맷: "item:{itemId}|opt:{optId1}x{qty1},{optId2}x{qty2}..."
     *  - SHA-256 해시로 64자 고정 문자열 반환
     */
    private String buildOptionComboHash(UUID itemId, List<CreateCartCommand.Option> options) {
        String base = "item:" + itemId + "|opt:" + normalizeOptions(options);
        return sha256Hex(base);
    }

    private String normalizeOptions(List<CreateCartCommand.Option> options) {
        if (options == null || options.isEmpty()) return "";
        return options.stream()
                .sorted(Comparator.comparing(o -> o.itemOptionId().toString()))
                .map(o -> o.itemOptionId() + "x" + o.quantity())
                .collect(Collectors.joining(","));
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder(dig.length * 2);
            for (byte b : dig) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
    
    @Override
    public CartSnapshot getCartByUserId(String userId) {
        // 사용자의 장바구니 조회 (items와 options까지 함께 로딩)
        Cart cart = cartRepository.findWithItemsByUserId(userId).orElse(null);
        
        if (cart == null) {
            return null; // 장바구니가 없음
        }
        
        // 장바구니가 있으면 CartSnapshot으로 변환
        return toSnapshot(cart);
    }
    
    @Override
    public CartSnapshot changeCartItemQuantity(String userId, UUID cartItemId, int quantity) {
        // 1. 수량 검증
        if (quantity < 0) {
            throw new InvalidQuantityException("수량은 0 이상이어야 합니다");
        }
        
        // 2. 장바구니 아이템 조회 및 권한 확인
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException("장바구니 아이템을 찾을 수 없습니다: " + cartItemId));
        
        // 3. 사용자 권한 확인 (본인의 장바구니인지 확인)
        Cart cart = cartItem.getCart();
        if (!Objects.equals(cart.getUserId(), userId)) {
            throw new ForbiddenCartAccessException();
        }
        
        // 4. 수량 변경 처리
        if (quantity == 0) {
            // 수량이 0이면 해당 아이템 삭제
            cartItemRepository.delete(cartItem);
        } else {
            // 수량 변경
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
        
        // 5. 업데이트된 장바구니 스냅샷 반환
        Cart updatedCart = cartRepository.findWithItemsByIdAndUserId(cart.getId(), userId)
                .orElseThrow(() -> new CartNotFoundException());
        
        return toSnapshot(updatedCart);
    }
}
