package com.sparta.sparta_eats.cart.presentation.dto.response;

import com.sparta.sparta_eats.cart.domain.entity.Cart;
import com.sparta.sparta_eats.cart.domain.entity.CartItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

/**
 * 장바구니 총액 계산 조회 API 응답 DTO
 * GET /v1/cart/{cartId}/totals
 */
@Schema(description = "장바구니 총액 계산 조회 응답")
@Getter
@Builder
public class CartTotalsResponse {

    @Schema(description = "장바구니 ID", example = "0f1d2a3b-4c5d-6e7f-8a9b-0c1d2e3f4a5b")
    private UUID cartId;
    
    @Schema(description = "장바구니 아이템 목록")
    private List<CartItemResponse> items;
    
    @Schema(description = "금액 요약 정보")
    private AmountsResponse amounts;

    @Schema(description = "장바구니 아이템 정보")
    @Getter
    @Builder
    public static class CartItemResponse {
        @Schema(description = "장바구니 아이템 ID", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d")
        private UUID cartItemId;
        
        @Schema(description = "상품 ID", example = "2b3c4d5e-6f7a-8b9c-0d1e-2f3a4b5c6d7e")
        private UUID itemId;
        
        @Schema(description = "상품명", example = "치킨버거")
        private String name;
        
        @Schema(description = "수량", example = "2")
        private Integer quantity;
        
        @Schema(description = "기본 단가 (p_item.sale_price)", example = "8000")
        private BigDecimal basePrice;
        
        @Schema(description = "옵션 총액 (p_item_option.add_price 합)", example = "1000")
        private BigDecimal optionsPrice;
        
        @Schema(description = "단가 합계 (basePrice + optionsPrice)", example = "9000")
        private BigDecimal unitPrice;
        
        @Schema(description = "항목 총액 (unitPrice x quantity)", example = "18000")
        private BigDecimal calculatedLinePrice;
        
        @Schema(description = "선택 옵션 목록")
        private List<SelectedOptionResponse> selectedOptions;
    }

    @Schema(description = "선택된 옵션 정보")
    @Getter
    @Builder
    public static class SelectedOptionResponse {
        @Schema(description = "옵션 ID", example = "3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f")
        private UUID optionId;
        
        @Schema(description = "옵션 추가 금액", example = "500")
        private BigDecimal addPrice;
        
        @Schema(description = "옵션명", example = "치즈 추가")
        private String optionName;
    }

    @Schema(description = "금액 요약 정보")
    @Getter
    @Builder
    public static class AmountsResponse {
        @Schema(description = "상품 총합(∑ calculatedLinePrice)", example = "18000")
        private BigDecimal itemsTotal;
        
        @Schema(description = "배달료(정책 적용)", example = "3000")
        private BigDecimal deliveryFee;
        
        @Schema(description = "할인 총액(현재 0)", example = "0")
        private BigDecimal discountTotal;
        
        @Schema(description = "결제 예정 총액 = itemsTotal + deliveryFee - discountTotal", example = "21000")
        private BigDecimal payableTotal;
    }

    /**
     * Cart 엔티티로부터 CartTotalsResponse 생성
     */
    public static CartTotalsResponse from(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(CartTotalsResponse::toCartItemResponse)
                .toList();

        // 금액 계산
        BigDecimal itemsTotal = itemResponses.stream()
                .map(CartItemResponse::getCalculatedLinePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal deliveryFee = BigDecimal.valueOf(3000); // 임시 고정 배달료
        BigDecimal discountTotal = BigDecimal.ZERO; // 현재 할인 없음
        BigDecimal payableTotal = itemsTotal.add(deliveryFee).subtract(discountTotal);

        AmountsResponse amounts = AmountsResponse.builder()
                .itemsTotal(itemsTotal)
                .deliveryFee(deliveryFee)
                .discountTotal(discountTotal)
                .payableTotal(payableTotal)
                .build();

        return CartTotalsResponse.builder()
                .cartId(cart.getId())
                .items(itemResponses)
                .amounts(amounts)
                .build();
    }

    private static CartItemResponse toCartItemResponse(CartItem cartItem) {
        // 옵션 총액 계산 (BigInteger -> BigDecimal 변환)
        BigDecimal optionsPrice = cartItem.getOptions().stream()
                .map(option -> new BigDecimal(option.getItemOption().getAddPrice().toString())
                        .multiply(BigDecimal.valueOf(option.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 단가 = 기본가격 + 옵션가격 (BigInteger -> BigDecimal 변환)
        BigInteger itemPrice = cartItem.getItem().getSalePrice() != null ? 
                cartItem.getItem().getSalePrice() : cartItem.getItem().getPrice();
        BigDecimal basePrice = new BigDecimal(itemPrice.toString());
        BigDecimal unitPrice = basePrice.add(optionsPrice);

        // 항목 총액 = 단가 × 수량
        BigDecimal calculatedLinePrice = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        // 선택된 옵션들 (BigInteger -> BigDecimal 변환)
        List<SelectedOptionResponse> selectedOptions = cartItem.getOptions().stream()
                .map(option -> SelectedOptionResponse.builder()
                        .optionId(option.getItemOption().getId())
                        .addPrice(new BigDecimal(option.getItemOption().getAddPrice().toString()))
                        .optionName(option.getItemOption().getName())
                        .build())
                .toList();

        return CartItemResponse.builder()
                .cartItemId(cartItem.getId())
                .itemId(cartItem.getItem().getId())
                .name(cartItem.getItem().getName())
                .quantity(cartItem.getQuantity())
                .basePrice(basePrice)
                .optionsPrice(optionsPrice)
                .unitPrice(unitPrice)
                .calculatedLinePrice(calculatedLinePrice)
                .selectedOptions(selectedOptions)
                .build();
    }
}
