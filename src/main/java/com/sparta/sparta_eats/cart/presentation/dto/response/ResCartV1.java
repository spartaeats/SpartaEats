package com.sparta.sparta_eats.cart.presentation.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ResCartV1(
        Boolean exists,
        UUID cartId,
        Store store,
        List<Item> items,
        Amounts amounts,
        UUID addressId,
        Instant createdAt,
        Instant updatedAt
) {
    public record Store(
            UUID id,
            String name
    ) {}
    
    public record Item(
            UUID cartItemId,
            UUID itemId,
            String name,
            int quantity,
            BigDecimal basePrice,
            BigDecimal optionsPrice,
            BigDecimal unitPrice,
            BigDecimal calculatedLinePrice,
            List<UUID> optionIds
    ) {}
    
    public record Amounts(
            BigDecimal itemsTotal,
            BigDecimal deliveryFee,
            BigDecimal discountTotal,
            BigDecimal payableTotal
    ) {}
}

