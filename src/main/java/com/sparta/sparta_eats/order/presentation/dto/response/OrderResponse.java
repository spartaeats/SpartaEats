package com.sparta.sparta_eats.order.presentation.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderResponse(
        UUID id,
        String status,
        LocalDateTime createdAt,
        Store store,
        List<Item> items,
        Amounts amounts,
        Delivery delivery,
        String contactPhone,
        Flags flags
        ) {

    public record Store(
            UUID id,
            String name) {
    }

    public record Item(
        UUID id,
        String name,
        Integer quantity,
        Long unitPrice,
        Long linePrice,
        List<Option> options

    ) {
    }

    public record Option(
            UUID id,
            String name,
            Integer price
    ) { }

    public record Amounts(
            String currency,
            Boolean vatIncluded,
            Long itemsTotal,
            Long deliveryFee,
            Long discountTotal,
            Long payableTotal
    ) { }

    public record Delivery(
            String addressSummary
    ) { }

    public record Flags(
            Boolean noCutlery,
            Boolean noSideDishes
    ) { }
}
