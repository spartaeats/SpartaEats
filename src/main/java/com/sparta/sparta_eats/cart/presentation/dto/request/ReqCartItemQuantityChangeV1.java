package com.sparta.sparta_eats.cart.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 장바구니 아이템 수량 변경 요청 DTO
 * 
 * API 스펙에 따라:
 * - quantity: 최종 수량 (0이면 해당 항목 삭제 처리)
 * - 0 ≤ quantity ≤ item.maxPerOrder
 */
public record ReqCartItemQuantityChangeV1(
        @NotNull(message = "수량은 필수입니다")
        @Min(value = 0, message = "수량은 0 이상이어야 합니다")
        Integer quantity
) {
}
