package com.sparta.sparta_eats.cart.presentation.controller;


import com.sparta.sparta_eats.cart.application.dto.CartSnapshot;
import com.sparta.sparta_eats.cart.application.dto.CreateCartCommand;
import com.sparta.sparta_eats.cart.application.dto.CreateCartResult;
import com.sparta.sparta_eats.cart.application.service.CartService;
import com.sparta.sparta_eats.cart.application.service.CartTotalsService;
import com.sparta.sparta_eats.cart.presentation.dto.request.ReqCartCreateV1;
import com.sparta.sparta_eats.cart.presentation.dto.request.ReqCartItemQuantityChangeV1;
import com.sparta.sparta_eats.cart.presentation.dto.response.ResCartV1;
import com.sparta.sparta_eats.item.domain.entity.Item;
import com.sparta.sparta_eats.store.domain.entity.Store;
import com.sparta.sparta_eats.item.domain.repository.ItemRepository;
import com.sparta.sparta_eats.store.domain.repository.StoreRepository;
import com.sparta.sparta_eats.cart.domain.entity.CartItemOption;
import com.sparta.sparta_eats.cart.infrastructure.repository.CartItemOptionRepository;
import com.sparta.sparta_eats.cart.infrastructure.repository.CartItemRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.sparta.sparta_eats.global.infrastructure.config.security.UserDetailsImpl;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * 장바구니 V1 컨트롤러
 *
 * - URL prefix: /v1/cart
 * - 인증: X-User-Id 헤더 또는 SecurityContext의 Authentication.name 를 userId로 사용(임시 방식)
 * - 주요 책임:
 *    1) 장바구니 생성/재사용/교체(POST /v1/cart)
 *    2) 장바구니 단건 조회(GET /v1/cart/{cartId})
 *    3) 장바구니 삭제(DELETE /v1/cart/{cartId})
 *
 */

@RestController
@RequestMapping("/v1/cart")
public class CartControllerV1 {

    private final CartService cartService;
    private final ItemRepository itemRepository;
    private final StoreRepository storeRepository;
    private final CartItemOptionRepository cartItemOptionRepository;
    private final CartItemRepository cartItemRepository;
    private final CartTotalsService cartTotalsService;

    public CartControllerV1(CartService cartService, ItemRepository itemRepository, StoreRepository storeRepository, CartItemOptionRepository cartItemOptionRepository, CartItemRepository cartItemRepository, CartTotalsService cartTotalsService) {
        this.cartService = cartService;
        this.itemRepository = itemRepository;
        this.storeRepository = storeRepository;
        this.cartItemOptionRepository = cartItemOptionRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartTotalsService = cartTotalsService;
    }

    /** 장바구니 생성/재사용 */
    @PostMapping
    public ResponseEntity<ResCartV1> createCart(
            @Valid @RequestBody ReqCartCreateV1 req,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        System.out.println("=== CartController Debug ===");
        System.out.println("userDetails: " + userDetails);
        System.out.println("userDetails.getUser(): " + (userDetails != null ? userDetails.getUser() : "null"));
        
        if (userDetails == null || userDetails.getUser() == null) {
            System.out.println("ERROR: userDetails or user is null!");
            return ResponseEntity.status(500).build();
        }
        
        String userId = userDetails.getUser().getUserId();
        System.out.println("userId: " + userId);

        // 1. 입력 검증
        if (req.storeId() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        if (req.items() != null) {
            for (ReqCartCreateV1.Item item : req.items()) {
                if (item.quantity() < 1) {
                    return ResponseEntity.badRequest().build();
                }
            }
        }

        // 2. forceReplace null 처리 (기본값 false) 다른 가게 장바구니 교체 여부
        boolean forceReplace = req.forceReplace() != null ? req.forceReplace() : false;

        // 3. CartService 호출
        CreateCartResult result = cartService.createOrGetCart(
                userId,
                req.storeId(),
                forceReplace,
                toCommandItems(req.items()),
                req.addressId()
        );

        // 4. 응답 DTO 생성
        ResCartV1 response = toResCart(result.cart());

        // 5. 상태 코드 분기
        if (result.created()) {
            // 새 장바구니 생성 → 201 Created + Location 헤더
            return ResponseEntity.created(
                    URI.create("/v1/cart/" + result.cart().id())
            ).body(response);
        } else {
            // 기존 장바구니 재사용 → 200 OK
            return ResponseEntity.ok(response);
        }
    }

    /**
     * [GET] /v1/cart — 장바구니 조회
     *
     * JWT 토큰의 userId로 본인 장바구니 조회
     * - 장바구니가 있으면: exists=true, 장바구니 정보 반환
     * - 장바구니가 없으면: exists=false, 빈 배열과 0원 반환
     */
    @GetMapping
    public ResponseEntity<ResCartV1> getCart(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        System.out.println("=== GET Cart Debug ===");
        System.out.println("userDetails: " + userDetails);
        
        if (userDetails == null || userDetails.getUser() == null) {
            System.out.println("ERROR: userDetails or user is null!");
            return ResponseEntity.status(500).build();
        }
        
        String userId = userDetails.getUser().getUserId();
        System.out.println("userId: " + userId);
        
        // 사용자의 장바구니 조회 (없으면 null)
        CartSnapshot snap = cartService.getCartByUserId(userId);
        
        if (snap == null) {
            // 장바구니가 없는 경우
            return ResponseEntity.ok(createEmptyCartResponse());
        } else {
            // 장바구니가 있는 경우
            return ResponseEntity.ok(toResCart(snap));
        }
    }

    /** 장바구니 삭제 */
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> deleteCart(
            @PathVariable UUID cartId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        String userId = userDetails.getUser().getUserId();
        cartService.deleteCart(userId, cartId);
        return ResponseEntity.noContent().build();
    }

    /**
     * [PUT] /v1/cart/items/{cartItemId}/quantity — 장바구니 아이템 수량 변경
     *
     * API 스펙에 따라:
     * - quantity=0이면 해당 항목 삭제 처리
     * - 0 ≤ quantity ≤ item.maxPerOrder
     * - 모든 항목이 삭제되어도 장바구니는 유지 (exists=true)
     */
    @PutMapping("/items/{cartItemId}/quantity")
    public ResponseEntity<ResCartV1> changeCartItemQuantity(
            @PathVariable UUID cartItemId,
            @Valid @RequestBody ReqCartItemQuantityChangeV1 req,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        String userId = userDetails.getUser().getUserId();
        
        // 수량 변경 처리
        CartSnapshot updatedCart = cartService.changeCartItemQuantity(userId, cartItemId, req.quantity());
        
        // 응답 생성
        ResCartV1 response = toResCart(updatedCart);
        
        return ResponseEntity.ok(response);
    }

    // ===== helper: mapping =====

    private List<CreateCartCommand.Item> toCommandItems(List<ReqCartCreateV1.Item> items) {
        if (items == null || items.isEmpty()) return List.of();
        return items.stream().map(i ->
                new CreateCartCommand.Item(
                        i.itemId(),
                        i.quantity(),
                        i.options() == null ? List.of() :
                                i.options().stream()
                                        .map(op -> new CreateCartCommand.Option(op.itemOptionId(), op.quantity()))
                                        .toList()
                )
        ).toList();
    }

    /**
     * 빈 장바구니 응답 생성
     */
    private ResCartV1 createEmptyCartResponse() {
        return new ResCartV1(
                false, // exists = false
                null,  // cartId = null
                null,  // store = null
                List.of(), // items = 빈 배열
                null, // amounts는 /totals에서 계산
                null, // addressId = null
                null, // createdAt = null
                null  // updatedAt = null
        );
    }

    /**
     * 장바구니가 있는 경우 응답 생성
     */
    private ResCartV1 toResCart(CartSnapshot s) {
        // 1. 매장 정보 조회
        Store store = storeRepository.findById(s.storeId())
                .orElse(new Store()); // 기본값으로 빈 객체
        
        // 2. 상품 정보들 조회
        List<UUID> itemIds = s.items().stream()
                .map(ci -> ci.itemId())
                .toList();
        List<Item> items = itemRepository.findAllById(itemIds);
        
        // 3. 상품별 상세 정보 (옵션과 가격 포함)
        List<ResCartV1.Item> resItems = s.items().stream().map(ci -> {
            // 해당 상품 찾기
            Item item = items.stream()
                    .filter(i -> i.getId().equals(ci.itemId()))
                    .findFirst()
                    .orElse(new Item()); // 기본값
            
            // 기본 가격 (할인가가 있으면 할인가, 없으면 정가)
            BigDecimal basePrice = item.getSalePrice() != null ? 
                    BigDecimal.valueOf(item.getSalePrice().longValue()) : 
                    BigDecimal.valueOf(item.getPrice().longValue());
            
            // 옵션 정보 조회 및 가격 계산 (JOIN FETCH 사용)
            List<ResCartV1.Item.Option> options = ci.options().stream().map(opt -> {
                // JOIN FETCH로 ItemOption을 함께 조회하여 LazyInitializationException 방지
                CartItemOption cartItemOption = cartItemOptionRepository.findByIdWithItemOption(opt.id()).orElse(null);
                
                if (cartItemOption != null && cartItemOption.getItemOption() != null) {
                    return new ResCartV1.Item.Option(
                            opt.id(),
                            cartItemOption.getItemOption().getName(),
                            BigDecimal.valueOf(cartItemOption.getItemOption().getAddPrice().longValue()),
                            opt.quantity()
                    );
                } else {
                    // 옵션 정보를 찾을 수 없는 경우 기본값
                    return new ResCartV1.Item.Option(
                            opt.id(),
                            "옵션명 없음",
                            BigDecimal.ZERO,
                            opt.quantity()
                    );
                }
            }).toList();
            
            // 옵션 총 가격 계산
            BigDecimal optionsPrice = options.stream()
                    .map(opt -> opt.addPrice().multiply(BigDecimal.valueOf(opt.quantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 단위 가격 (기본가 + 옵션가)
            BigDecimal unitPrice = basePrice.add(optionsPrice);
            
            // 라인 총 가격 (단위가 * 수량)
            BigDecimal calculatedLinePrice = unitPrice.multiply(BigDecimal.valueOf(ci.quantity()));
            
            return new ResCartV1.Item(
                    ci.id(), // cartItemId
                    item.getName() != null ? item.getName() : "상품명 없음",
                    ci.quantity(),
                    basePrice,
                    options,
                    optionsPrice,
                    unitPrice,
                    calculatedLinePrice
            );
        }).toList();
        
        // 4. 전체 가격 계산 (CartTotalsService 사용)
        ResCartV1.Amounts amounts = null;
        try {
            // CartTotalsService를 사용하여 전체 가격 계산
            // 여기서는 간단하게 items의 총합만 계산
            BigDecimal itemsTotal = resItems.stream()
                    .map(ResCartV1.Item::calculatedLinePrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 기본 배송비 (예: 3000원)
            BigDecimal deliveryFee = itemsTotal.compareTo(BigDecimal.valueOf(20000)) > 0 ? 
                    BigDecimal.ZERO : BigDecimal.valueOf(3000);
            
            amounts = new ResCartV1.Amounts(
                    itemsTotal,
                    deliveryFee,
                    BigDecimal.ZERO, // 할인 총액
                    itemsTotal.add(deliveryFee) // 결제 총액
            );
        } catch (Exception e) {
            // 가격 계산 실패 시 null로 설정
            amounts = null;
        }
        
        return new ResCartV1(
                true, // exists = true
                s.id(), // cartId
                new ResCartV1.Store(s.storeId(), store.getName() != null ? store.getName() : "매장명 없음"),
                resItems,
                amounts, // 계산된 가격 정보
                s.addressId(), // addressId
                s.createdAt(),
                s.updatedAt()
        );
    }

}
