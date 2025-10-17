package com.sparta.sparta_eats.order.presentation.dto.request;

import lombok.Builder;

@Builder
public record OrderSearchCondition(
        String address,
        String monthFrom,
        String monthTo,
        String orderOutcome,
        String q
) {
}
