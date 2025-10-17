package com.sparta.sparta_eats.payment.application.service;

import com.sparta.sparta_eats.payment.presentation.dto.response.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    UUID create(String idempotencyKey, UUID orderId, String userId, BigDecimal amount);
    void confirm(UUID paymentId, String paymentKey);
    void cancel(UUID paymentId, String reason);

    Page<PaymentResponse> search(Pageable pageable, Boolean includeDeleted);
}
