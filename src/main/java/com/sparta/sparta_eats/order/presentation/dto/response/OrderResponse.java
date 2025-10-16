package com.sparta.sparta_eats.order.presentation.dto.response;

import com.sparta.sparta_eats.order.domain.entity.Order;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
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

    public record ItemResponse(
            String name,
            Integer quantity,
            String optionsText
    ) { }
}
