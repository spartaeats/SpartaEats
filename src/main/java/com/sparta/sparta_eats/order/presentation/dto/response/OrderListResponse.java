package com.sparta.sparta_eats.order.presentation.dto.response;

import com.sparta.sparta_eats.order.domain.entity.Order;
import lombok.Builder;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderListResponse(
        UUID id,
        UUID storeId,
        String storeName,
        String storeImage,
        List<ItemResponse> items,
        BigDecimal totalAmount,
        Order.OrderStatus status,
        LocalDateTime createdAt,
        Pageable pageable,
        Long totalElements,
        Integer totalPages,
        Boolean hasNext) {

    @Builder
    public record ItemResponse(
            String name,
            Integer quantity,
            String optionsText
    ) { }
}
