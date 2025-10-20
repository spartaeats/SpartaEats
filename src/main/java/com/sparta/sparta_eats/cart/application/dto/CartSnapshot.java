package com.sparta.sparta_eats.cart.application.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

//서비스 출력 전용 DTO: 가격 정보 포함
public record CartSnapshot(
        UUID id,
        String userId,
        UUID storeId,
        List<CartItemSnapshot> items,
        Instant createdAt,
        Instant updatedAt,
        UUID addressId
) {
    public record CartItemSnapshot(
            UUID id,
            UUID itemId,
            int quantity,
            String optionComboHash,
            List<CartItemOptionSnapshot> options,
            Instant createdAt,
            Instant updatedAt
    ) {}

    public record CartItemOptionSnapshot(
            UUID id,
            UUID itemOptionId,
            int quantity
    ) {}
}

