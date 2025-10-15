package com.sparta.sparta_eats.order.domain.dto;

import lombok.Builder;

import java.math.BigInteger;

@Builder
public record OrderSnapshotDto(BigInteger itemTotal,
                               BigInteger deliveryFee,
                               BigInteger discountTotal,
                               BigInteger totalAmount) {
}
