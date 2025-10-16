package com.sparta.sparta_eats.order.presentation.dto.response;

import com.sparta.sparta_eats.order.domain.entity.Order;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderCreateResponse(
        UUID id,
        Order.OrderStatus status,
        LocalDateTime createdAt,
        StoreResponse store,
        List<ItemResponse> items,
        Amounts amounts,
        Delivery delivery,
        String contactPhone,
        Flags flags
        ) {

    @Builder
    public record StoreResponse(
            UUID id,
            String name) {
    }

    @Builder
    public record ItemResponse(
        UUID id,
        String name,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal linePrice,
        List<OptionResponse> options

    ) {
    }

    @Builder
    public record OptionResponse(
            UUID id,
            String name,
            BigDecimal price
    ) { }

    @Builder
    public record Amounts(
            Currency currency,
            Boolean vatIncluded,
            BigDecimal itemsTotal,
            BigDecimal deliveryFee,
            BigDecimal discountTotal,
            BigDecimal payableTotal
    ) { }

    @Builder
    public record Delivery(
            String addressSummary
    ) { }

    @Builder
    public record Flags(
            Boolean noCutlery,
            Boolean noSideDishes
    ) { }
}
