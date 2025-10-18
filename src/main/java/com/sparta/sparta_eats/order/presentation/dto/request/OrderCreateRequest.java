package com.sparta.sparta_eats.order.presentation.dto.request;

import com.sparta.sparta_eats.order.domain.entity.Order;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record OrderCreateRequest(
        @NotNull(message = "{validation.order.storeId.notNull}")
        UUID storeId,
        @NotEmpty(message = "{validation.order.items.notEmpty}")
        List<OrderItemRequest> items,
        @NotNull(message = "{validation.order.addressId.notNull}")
        UUID addressId,
        String memoToOwner,
        String memoToRider,
        String contactPhone,
        Boolean noCutlery,
        Boolean noSideDishes,
        String source,
        Order.FulfillmentType fulfillmentType) {

    public OrderCreateRequest {
        if (source.isBlank()) {
            source = "APP";
        }
    }

    public record OrderItemRequest(
            @NotNull(message = "{validation.order.items.id.notNull}")
            UUID id,
            @NotNull(message = "{validation.order.items.quantity.notNull}")
            Integer quantity,
            List<OrderItemOptionRequest> options

    ) {
        public record OrderItemOptionRequest(
                @NotNull(message = "{validation.order.items.options.optionId.notNull}")
                UUID optionId
        ) {
        }
    }
}
