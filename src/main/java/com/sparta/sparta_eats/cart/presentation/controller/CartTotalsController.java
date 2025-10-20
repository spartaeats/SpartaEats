package com.sparta.sparta_eats.cart.presentation.controller;

import com.sparta.sparta_eats.cart.application.service.CartTotalsService;
import com.sparta.sparta_eats.cart.presentation.dto.response.CartTotalsResponse;
import com.sparta.sparta_eats.global.infrastructure.config.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 장바구니 총액 계산 컨트롤러
 */
@Tag(name = "Cart Totals", description = "장바구니 총액 계산 API")
@RestController
@RequestMapping("/v1/cart")
@RequiredArgsConstructor
public class CartTotalsController {

    private final CartTotalsService cartTotalsService;

    /**
     * 장바구니 총액 계산 조회
     * 
     * @param cartId 장바구니 ID
     * @param userDetails 인증된 사용자 정보
     * @return 장바구니 총액 정보
     */
    @Operation(
        summary = "장바구니 총액 계산 조회",
        description = """
            장바구니의 상품 총합, 배달료, 할인금액, 결제 예정 총액을 계산하여 반환합니다.
            
            **계산 로직:**
            - unitPrice = basePrice + optionsPrice
            - calculatedLinePrice = unitPrice × quantity  
            - itemsTotal = ∑(calculatedLinePrice)
            - payableTotal = itemsTotal + deliveryFee - discountTotal
            
            **배달료:** 현재 임시로 3,000원 고정
            **할인금액:** 현재 0원
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CartTotalsResponse.class),
                examples = @ExampleObject(
                    name = "성공 응답 예시",
                    value = """
                        {
                          "cartId": "0f1d2a3b-4c5d-6e7f-8a9b-0c1d2e3f4a5b",
                          "items": [
                            {
                              "cartItemId": "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d",
                              "itemId": "2b3c4d5e-6f7a-8b9c-0d1e-2f3a4b5c6d7e",
                              "name": "치킨버거",
                              "quantity": 2,
                              "basePrice": 8000,
                              "optionsPrice": 1000,
                              "unitPrice": 9000,
                              "calculatedLinePrice": 18000,
                              "selectedOptions": [
                                {
                                  "optionId": "3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f",
                                  "addPrice": 500,
                                  "optionName": "치즈 추가"
                                }
                              ]
                            }
                          ],
                          "amounts": {
                            "itemsTotal": 18000,
                            "deliveryFee": 3000,
                            "discountTotal": 0,
                            "payableTotal": 21000
                          }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "인증 실패 - 토큰 없음/무효",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "인증 실패",
                    value = """
                        {
                          "code": "UNAUTHORIZED",
                          "message": "unauthorized"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "권한 없음 - CUSTOMER 아님",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "권한 없음",
                    value = """
                        {
                          "code": "FORBIDDEN",
                          "message": "role not allowed"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "장바구니 없음 - cartId 미존재 또는 소유권 불일치",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "장바구니 없음",
                    value = """
                        {
                          "code": "CART_NOT_FOUND",
                          "message": "cart not found"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "서버 오류",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "서버 오류",
                    value = """
                        {
                          "code": "INTERNAL_ERROR",
                          "message": "internal server error"
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/{cartId}/totals")
    public ResponseEntity<CartTotalsResponse> getCartTotals(
            @Parameter(description = "장바구니 ID", required = true)
            @PathVariable UUID cartId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        CartTotalsResponse response = cartTotalsService.getCartTotals(
                userDetails.getUsername(), 
                cartId
        );
        
        return ResponseEntity.ok(response);
    }
}
