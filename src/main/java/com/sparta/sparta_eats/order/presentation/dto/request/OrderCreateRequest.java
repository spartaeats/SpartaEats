package com.sparta.sparta_eats.order.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record OrderCreateRequest(
        @NotNull
        UUID storeId,
        @NotEmpty
        List<OrderItemRequest> items,
        @NotNull
        UUID addressId,
        String memoToOwner,
        String memoToRider,
        String contactPhone,
        Boolean noCutlery,
        Boolean noSideDishes,
        String source) {

    public OrderCreateRequest {
        if (source.isBlank()) {
            source = "APP";
        }
    }

    public record OrderItemRequest(
            @NotNull
            UUID id,
            @NotNull
            Integer quantity,
            List<OrderItemOptionRequest> options

    ) {
        public record OrderItemOptionRequest(
                @NotNull
                UUID optionId
        ) {
        }
    }
}
