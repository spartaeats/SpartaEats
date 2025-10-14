package com.sparta.sparta_eats.cart.presentation.dto.response;


import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ResCartV1(
        UUID id,
        String userId,
        UUID storeId,
        List<Item> items,
        Instant createdAt,
        Instant updatedAt
) {
    public record Item(
            UUID id,
            UUID itemId,
            int quantity,
            String optionComboHash,
            List<Option> options,
            Instant createdAt,
            Instant updatedAt
    ) {}
    public record Option(
            UUID id,
            UUID itemOptionId,
            int quantity
    ) {}
}

