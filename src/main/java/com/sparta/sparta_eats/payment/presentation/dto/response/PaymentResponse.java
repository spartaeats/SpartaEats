package com.sparta.sparta_eats.payment.presentation.dto.response;

import com.sparta.sparta_eats.payment.domain.model.PaymentMethod;
import com.sparta.sparta_eats.payment.domain.model.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID orderId,
        UUID userId,
        BigDecimal amount,
        PaymentMethod method,
        PaymentStatus status,
        LocalDateTime createdAt,
        LocalDateTime confirmedAt,
        LocalDateTime canceledAt
) { }
