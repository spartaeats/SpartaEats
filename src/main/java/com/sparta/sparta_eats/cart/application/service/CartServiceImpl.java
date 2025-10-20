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
import com.sparta.sparta_eats.cart.infrastructure.repository.CartItemRepository;
import com.sparta.sparta_eats.cart.infrastructure.repository.CartItemOptionRepository;
import com.sparta.sparta_eats.cart.infrastructure.repository.CartRepository;
import com.sparta.sparta_eats.item.domain.entity.Item;
import com.sparta.sparta_eats.item.domain.repository.ItemRepository;
import com.sparta.sparta_eats.store.domain.entity.Store;
import com.sparta.sparta_eats.store.domain.repository.StoreRepository;
import com.sparta.sparta_eats.item.domain.repository.ItemOptionRepository;
import com.sparta.sparta_eats.user.domain.entity.User;
import com.sparta.sparta_eats.user.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemOptionRepository cartItemOptionRepository;
    private final ItemOptionRepository itemOptionRepository;

    public CartServiceImpl(UserRepository userRepository,
                           StoreRepository storeRepository,
                           ItemRepository itemRepository,
                           CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           CartItemOptionRepository cartItemOptionRepository,
                           ItemOptionRepository itemOptionRepository) {
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartItemOptionRepository = cartItemOptionRepository;
        this.itemOptionRepository = itemOptionRepository;
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
                                            List<CreateCartCommand.Item> items,
                                            UUID addressId) {

        System.out.println("=== CartService Debug ===");
        System.out.println("userId: " + userId);
        System.out.println("storeId: " + storeId);
        System.out.println("forceReplace: " + forceReplace);
        System.out.println("items: " + items);

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
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found: " + storeId));

        boolean created = false;
        boolean reused  = false;
        boolean replaced = false;

        // --- 2) 사용자 현재 카트 조회 ---
        Cart cart = cartRepository.findByUser(user).orElse(null);

        // --- 3) 다른 매장 카트면 교체 여부 판단 ---
        if (cart != null && !Objects.equals(cart.getStore().getId(), store.getId())) {
            if (!forceReplace) {
                throw new StoreMismatchException(cart.getStore().getId(), store.getId()); // 컨트롤러에서 409 매핑
            }
            cartRepository.delete(cart); // CASCADE 로 하위 모두 삭제
            cartRepository.flush();
            cart = null;
            replaced = true;
        }

        // --- 4) 카트가 없으면 Builder로 생성 ---
        if (cart == null) {
            cart = Cart.builder()
                    .user(user)             // User 엔티티 사용
                    .store(store)           // Store 엔티티 사용
                    .addressId(addressId)   // Address ID 설정
                    .build();
            cart = cartRepository.save(cart);
            created = true;
        } else {
            // 기존 카트의 addressId 업데이트
            cart.setAddressId(addressId);
            cartRepository.save(cart);
            reused = true;
        }

        // --- 5) 초기 아이템 병합(merge) ---
        if (items != null && !items.isEmpty()) {
            for (CreateCartCommand.Item reqItem : items) {
                // Item 엔티티 조회
                Item item = itemRepository.findById(reqItem.itemId())
                        .orElseThrow(() -> new IllegalArgumentException("Item not found: " + reqItem.itemId()));

                // 동일한 아이템이 이미 있는지 확인 (간단하게 itemId로만 확인)
                CartItem existingItem = cart.getItems().stream()
                        .filter(ci -> ci.getItem().getId().equals(reqItem.itemId()))
                        .findFirst()
                        .orElse(null);

                if (existingItem == null) {
                    // 새 아이템 추가
                    CartItem newItem = CartItem.builder()
                            .item(item)
                            .quantity(reqItem.quantity())
                            .itemPrice(new BigDecimal(item.getPrice().toString()))
                            .build();
                    newItem.setCart(cart);
                    cartItemRepository.save(newItem);
                    
                    // 옵션 정보 저장
                    if (reqItem.options() != null && !reqItem.options().isEmpty()) {
                        for (CreateCartCommand.Option reqOption : reqItem.options()) {
                            // ItemOption 엔티티 조회
                            com.sparta.sparta_eats.store.domain.entity.ItemOption itemOption = 
                                    itemOptionRepository.findById(reqOption.itemOptionId())
                                            .orElseThrow(() -> new IllegalArgumentException("ItemOption not found: " + reqOption.itemOptionId()));
                            
                            // CartItemOption 생성 및 저장
                            CartItemOption cartItemOption = CartItemOption.builder()
                                    .itemOption(itemOption)
                                    .quantity(reqOption.quantity())
                                    .build();
                            cartItemOption.attachTo(newItem);
                            cartItemOptionRepository.save(cartItemOption);
                        }
                    }
                } else {
                    // 기존 아이템 수량 증가
                    existingItem.increaseQuantity(reqItem.quantity());
                    
                    // 기존 아이템에 옵션 정보가 없고 요청에 옵션이 있으면 추가
                    if (reqItem.options() != null && !reqItem.options().isEmpty()) {
                        // 기존 옵션들 확인
                        List<CartItemOption> existingOptions = cartItemOptionRepository.findByCartItem(existingItem);
                        if (existingOptions.isEmpty()) {
                            // 기존 옵션이 없으면 새로 추가
                            for (CreateCartCommand.Option reqOption : reqItem.options()) {
                                // ItemOption 엔티티 조회
                                com.sparta.sparta_eats.store.domain.entity.ItemOption itemOption = 
                                        itemOptionRepository.findById(reqOption.itemOptionId())
                                                .orElseThrow(() -> new IllegalArgumentException("ItemOption not found: " + reqOption.itemOptionId()));
                                
                                // CartItemOption 생성 및 저장
                                CartItemOption cartItemOption = CartItemOption.builder()
                                        .itemOption(itemOption)
                                        .quantity(reqOption.quantity())
                                        .build();
                                cartItemOption.attachTo(existingItem);
                                cartItemOptionRepository.save(cartItemOption);
                            }
                        }
                    }
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
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Cart cart = cartRepository.findWithItemsByIdAndUser(cartId, user)
                .orElseThrow(CartNotFoundException::new);
        return toSnapshot(cart);
    }

    @Override
    public void deleteCart(String userId, UUID cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(CartNotFoundException::new);
        if (!Objects.equals(cart.getUser().getUserId(), userId)) {
            throw new ForbiddenCartAccessException();
        }
        cartRepository.delete(cart); // CASCADE
    }

    // ================== 내부 유틸 ==================

    private CartSnapshot toSnapshot(Cart cart) {
        System.out.println("=== toSnapshot Debug ===");
        System.out.println("Cart ID: " + cart.getId());
        System.out.println("Cart items count: " + cart.getItems().size());
        
        // cart.getItems() 가 LAZY라면 Controller에서 EntityGraph로 불러오거나
        // CartRepository의 findWithItems... 를 통해 항상 items/option을 로딩하도록 사용하세요.
        List<CartSnapshot.CartItemSnapshot> itemSnaps = cart.getItems().stream()
                .map(ci -> {
                    System.out.println("Processing CartItem: " + ci.getId() + ", Item: " + ci.getItem().getName());
                    
                    // 옵션을 별도로 로딩 (JOIN FETCH로 LazyInitializationException 방지)
                    List<CartItemOption> foundOptions = cartItemOptionRepository.findByCartItemWithItemOption(ci);
                    System.out.println("Found options for CartItem " + ci.getId() + ": " + foundOptions.size());
                    
                    List<CartSnapshot.CartItemOptionSnapshot> optionSnaps = foundOptions
                            .stream()
                            .map(op -> {
                                System.out.println("Processing option: " + op.getId() + ", ItemOption: " + op.getItemOption().getId() + ", Quantity: " + op.getQuantity());
                                return new CartSnapshot.CartItemOptionSnapshot(
                                        op.getId(), op.getItemOption().getId(), op.getQuantity()
                                );
                            })
                            .toList();
                    
                    System.out.println("CartItem options count: " + optionSnaps.size());
                    
                    return new CartSnapshot.CartItemSnapshot(
                            ci.getId(),
                            ci.getItem().getId(),
                            ci.getQuantity(),
                            "", // optionComboHash는 더 이상 사용하지 않음
                            optionSnaps,
                            asInstant(ci.getCreatedAt()),
                            asInstant(ci.getUpdatedAt())
                    );
                })
                .toList();
        
        System.out.println("Final itemSnaps count: " + itemSnaps.size());

        return new CartSnapshot(
                cart.getId(),
                cart.getUser().getUserId(),
                cart.getStore().getId(),
                itemSnaps,
                asInstant(cart.getCreatedAt()),
                asInstant(cart.getUpdatedAt()),
                cart.getAddressId()
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

    
    @Override
    public CartSnapshot getCartByUserId(String userId) {
        // 사용자 조회
        User user = userRepository.findByUserId(userId).orElse(null);
        if (user == null) {
            return null;
        }
        
        // 사용자의 장바구니 조회 (items와 options까지 함께 로딩)
        Cart cart = cartRepository.findWithItemsByUser(user).orElse(null);
        
        if (cart == null) {
            return null; // 장바구니가 없음
        }
        
        // 장바구니가 있으면 CartSnapshot으로 변환
        return toSnapshot(cart);
    }
    
    @Override
    public CartSnapshot changeCartItemQuantity(String userId, UUID cartItemId, int quantity) {
        System.out.println("=== changeCartItemQuantity Debug ===");
        System.out.println("userId: " + userId);
        System.out.println("cartItemId: " + cartItemId);
        System.out.println("requested quantity: " + quantity);
        
        // 1. 수량 검증
        if (quantity < 0) {
            throw new InvalidQuantityException("수량은 0 이상이어야 합니다");
        }
        
        // 2. 장바구니 아이템 조회 및 권한 확인
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException("장바구니 아이템을 찾을 수 없습니다: " + cartItemId));
        
        System.out.println("Found cartItem: " + cartItem.getId() + ", current quantity: " + cartItem.getQuantity());
        
        // 3. 사용자 권한 확인 (본인의 장바구니인지 확인)
        Cart cart = cartItem.getCart();
        if (!Objects.equals(cart.getUser().getUserId(), userId)) {
            throw new ForbiddenCartAccessException();
        }
        
        // 4. 수량 변경 처리
        if (quantity == 0) {
            // 수량이 0이면 해당 아이템 삭제
            System.out.println("Deleting cartItem (quantity = 0)");
            cartItemRepository.delete(cartItem);
        } else {
            // 수량 변경
            int currentQuantity = cartItem.getQuantity();
            int quantityDiff = quantity - currentQuantity;
            System.out.println("Current quantity: " + currentQuantity + ", quantity diff: " + quantityDiff);
            
            cartItem.increaseQuantity(quantityDiff);
            System.out.println("After increaseQuantity, new quantity: " + cartItem.getQuantity());
        }
        
        // 5. 업데이트된 장바구니 스냅샷 반환
        Cart updatedCart = cartRepository.findWithItemsByIdAndUser(cart.getId(), cart.getUser())
                .orElseThrow(() -> new CartNotFoundException());
        
        System.out.println("Updated cart items count: " + updatedCart.getItems().size());
        return toSnapshot(updatedCart);
    }
}
