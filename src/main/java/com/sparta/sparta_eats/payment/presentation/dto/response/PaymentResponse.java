package com.sparta.sparta_eats.payment.presentation.dto.response;

import com.sparta.sparta_eats.payment.domain.model.PaymentMethod;
import com.sparta.sparta_eats.payment.domain.model.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID orderId,
        UUID userId,
        long amount,
        PaymentMethod method,
        PaymentStatus status,
        LocalDateTime confirmedAt,
        LocalDateTime canceledAt
) { }
