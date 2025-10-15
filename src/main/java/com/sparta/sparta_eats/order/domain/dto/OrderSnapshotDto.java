package com.sparta.sparta_eats.order.domain.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderSnapshotDto(BigDecimal itemTotal,
                               BigDecimal deliveryFee,
                               BigDecimal discountTotal,
                               BigDecimal totalAmount) {
}
