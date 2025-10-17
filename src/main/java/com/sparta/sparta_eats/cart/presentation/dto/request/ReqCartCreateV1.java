package com.sparta.sparta_eats.cart.presentation.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ReqCartCreateV1(
        @NotNull UUID storeId,
        Boolean forceReplace,
        List<Item> items,
        @NotNull UUID addressId
) {
    public record Item(
            @NotNull UUID itemId,
            @Min(1) int quantity,
            List<Option> options
    ) {}
    public record Option(
            @NotNull UUID itemOptionId,
            @Min(1) int quantity
    ) {}
}

