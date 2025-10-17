package com.sparta.sparta_eats.cart.presentation.controller;


import com.sparta.sparta_eats.cart.application.dto.CartSnapshot;
import com.sparta.sparta_eats.cart.application.dto.CreateCartCommand;
import com.sparta.sparta_eats.cart.application.dto.CreateCartResult;
import com.sparta.sparta_eats.cart.application.service.CartService;
import com.sparta.sparta_eats.cart.presentation.dto.request.ReqCartCreateV1;
import com.sparta.sparta_eats.cart.presentation.dto.request.ReqCartItemQuantityChangeV1;
import com.sparta.sparta_eats.cart.presentation.dto.response.ResCartV1;
import com.sparta.sparta_eats.cart.presentation.dto.response.ResCreateCartResultV1;
import com.sparta.sparta_eats.store.entity.Item;
import com.sparta.sparta_eats.store.entity.Store;
import com.sparta.sparta_eats.store.infrastructure.repository.ItemRepository;
import com.sparta.sparta_eats.store.infrastructure.repository.StoreRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public CartControllerV1(CartService cartService, ItemRepository itemRepository, StoreRepository storeRepository) {
        this.cartService = cartService;
        this.itemRepository = itemRepository;
        this.storeRepository = storeRepository;
    }

    /** 장바구니 생성/재사용 */
    @PostMapping
    public ResponseEntity<ResCreateCartResultV1> createCart(
            @Valid @RequestBody ReqCartCreateV1 req,
            // 프로젝트마다 다르니 편의상 헤더로도 받을 수 있게 둠(선택)
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader
    ) {
        String userId = resolveUserId(userIdHeader);

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
                toCommandItems(req.items())
        );

        // 4. 응답 DTO 생성
        ResCreateCartResultV1 response = new ResCreateCartResultV1(
                toResCart(result.cart()),
                result.created(),
                result.reused(),
                result.replacedPreviousStoreCart()
        );

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
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader
    ) {
        String userId = resolveUserId(userIdHeader);
        
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
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader
    ) {
        String userId = resolveUserId(userIdHeader);
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
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader
    ) {
        String userId = resolveUserId(userIdHeader);
        
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
                new ResCartV1.Amounts(
                        BigDecimal.ZERO, // itemsTotal = 0
                        BigDecimal.ZERO, // deliveryFee = 0
                        BigDecimal.ZERO, // discountTotal = 0
                        BigDecimal.ZERO  // payableTotal = 0
                ),
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
        Store store = storeRepository.findById(s.storeId().toString())
                .orElse(new Store()); // 기본값으로 빈 객체
        
        // 2. 상품 정보들 조회
        List<String> itemIds = s.items().stream()
                .map(ci -> ci.itemId().toString())
                .toList();
        List<Item> items = itemRepository.findByIdIn(itemIds);
        
        // 3. 상품별 가격 계산
        List<ResCartV1.Item> resItems = s.items().stream().map(ci -> {
            // 해당 상품 찾기
            Item item = items.stream()
                    .filter(i -> i.getId().equals(ci.itemId().toString()))
                    .findFirst()
                    .orElse(new Item()); // 기본값
            
            // 가격 계산 (간단하게)
            BigDecimal basePrice = item.getPrice() != null ? 
                    new BigDecimal(item.getPrice().toString()) : BigDecimal.ZERO;
            BigDecimal optionsPrice = BigDecimal.ZERO; // 옵션은 나중에 구현
            BigDecimal unitPrice = basePrice.add(optionsPrice);
            BigDecimal calculatedLinePrice = unitPrice.multiply(BigDecimal.valueOf(ci.quantity()));
            
            // 옵션 ID 목록 (간단하게 빈 배열)
            List<UUID> optionIds = List.of(); // TODO: 실제 옵션 ID 조회
            
            return new ResCartV1.Item(
                    ci.id(), // cartItemId
                    ci.itemId(), // itemId
                    item.getName() != null ? item.getName() : "상품명 없음",
                    ci.quantity(),
                    basePrice,
                    optionsPrice,
                    unitPrice,
                    calculatedLinePrice,
                    optionIds
            );
        }).toList();
        
        // 4. 총 금액 계산
        BigDecimal itemsTotal = resItems.stream()
                .map(ResCartV1.Item::calculatedLinePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal deliveryFee = BigDecimal.valueOf(3000); // 기본 배달비
        BigDecimal discountTotal = BigDecimal.ZERO; // 할인 없음
        BigDecimal payableTotal = itemsTotal.add(deliveryFee).subtract(discountTotal);
        
        return new ResCartV1(
                true, // exists = true
                s.id(), // cartId
                new ResCartV1.Store(s.storeId(), store.getName() != null ? store.getName() : "매장명 없음"),
                resItems,
                new ResCartV1.Amounts(
                        itemsTotal,
                        deliveryFee,
                        discountTotal,
                        payableTotal
                ),
                null, // addressId는 나중에 구현
                s.createdAt(),
                s.updatedAt()
        );
    }

    // 보안 컨텍스트에서 userId 추출(임시). 프로젝트 보안 설정에 맞게 교체 가능.
    private String resolveUserId(String headerUserId) {
        if (headerUserId != null && !headerUserId.isBlank()) return headerUserId;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) return auth.getName(); // username을 userId로 사용
        throw new IllegalStateException("Cannot resolve userId. Provide X-User-Id header or configure security principal.");
        // 실제 프로젝트에선 @AuthenticationPrincipal(expression="userId") String userId 형태로 쓰는 걸 권장
    }
}
