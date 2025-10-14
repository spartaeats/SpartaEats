package com.sparta.sparta_eats.cart.presentation.controller;


import com.sparta.sparta_eats.cart.application.dto.CartSnapshot;
import com.sparta.sparta_eats.cart.application.dto.CreateCartCommand;
import com.sparta.sparta_eats.cart.application.dto.CreateCartResult;
import com.sparta.sparta_eats.cart.application.service.CartService;
import com.sparta.sparta_eats.cart.presentation.dto.request.ReqCartCreateV1;
import com.sparta.sparta_eats.cart.presentation.dto.response.ResCartV1;
import com.sparta.sparta_eats.cart.presentation.dto.response.ResCreateCartResultV1;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/cart")
public class CartControllerV1 {

    private final CartService cartService;

    public CartControllerV1(CartService cartService) {
        this.cartService = cartService;
    }

    /** 장바구니 생성/재사용 */
    @PostMapping
    public ResponseEntity<ResCreateCartResultV1> createCart(
            @Valid @RequestBody ReqCartCreateV1 req,
            // 프로젝트마다 다르니 편의상 헤더로도 받을 수 있게 둠(선택)
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader
    ) {
        String userId = resolveUserId(userIdHeader);

        CreateCartResult result = cartService.createOrGetCart(
                userId,
                req.storeId(),
                Boolean.TRUE.equals(req.forceReplace()),
                toCommandItems(req.items())
        );

        return ResponseEntity.ok(
                new ResCreateCartResultV1(
                        toResCart(result.cart()),
                        result.created(),
                        result.reused(),
                        result.replacedPreviousStoreCart()
                )
        );
    }

    /** 장바구니 조회 */
    @GetMapping("/{cartId}")
    public ResponseEntity<ResCartV1> getCart(
            @PathVariable UUID cartId,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader
    ) {
        String userId = resolveUserId(userIdHeader);
        CartSnapshot snap = cartService.getCart(userId, cartId);
        return ResponseEntity.ok(toResCart(snap));
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

    private ResCartV1 toResCart(CartSnapshot s) {
        return new ResCartV1(
                s.id(), s.userId(), s.storeId(),
                s.items().stream().map(ci ->
                        new ResCartV1.Item(
                                ci.id(), ci.itemId(), ci.quantity(), ci.optionComboHash(),
                                ci.options().stream()
                                        .map(op -> new ResCartV1.Option(op.id(), op.itemOptionId(), op.quantity()))
                                        .toList(),
                                ci.createdAt(), ci.updatedAt()
                        )
                ).toList(),
                s.createdAt(), s.updatedAt()
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
