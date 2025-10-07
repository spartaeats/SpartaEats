package com.sparta.sparta_eats.address.presentation.dto.request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TmapTimeRequest(BigDecimal startX,
                              BigDecimal startY,
                              BigDecimal endX,
                              BigDecimal endY) {
}