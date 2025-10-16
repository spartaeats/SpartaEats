package com.sparta.sparta_eats.order.presentation.dto.response;
import com.sparta.sparta_eats.order.domain.entity.Order;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderSingleResponse(
        UUID id,
        Order.OrderStatus status,
        StoreResponse store,
        List<ItemResponse> items,
        AmountsResponse amounts,
        DeliveryResponse delivery,
        String contactPhone,
        FlagsResponse flags,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    @Builder
    public record StoreResponse(
            UUID id,
            String name
    ) {}

    @Builder
    public record ItemResponse(
            UUID id,
            String name,
            BigDecimal basePrice,
            BigDecimal optionsPrice,
            BigDecimal unitPrice,
            Integer quantity,
            BigDecimal calculatedLinePrice,
            List<OptionResponse> options
    ) {}

    @Builder
    public record OptionResponse(
            UUID optionId,
            String name
    ) {}

    @Builder
    public record AmountsResponse(
            BigDecimal itemsTotal,
            BigDecimal deliveryFee,
            BigDecimal discountTotal,
            BigDecimal payableTotal
    ) {}

    @Builder
    public record DeliveryResponse(
            String addressSummary
    ) {}

    @Builder
    public record FlagsResponse(
            Boolean noCutlery,
            Boolean noSideDishes
    ) {}
}
