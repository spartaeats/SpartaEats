package com.sparta.sparta_eats.payment.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PaymentService {
    UUID create(String idempotencyKey, UUID orderId, UUID userId, long amount);
    void confirm(UUID paymentId, String paymentKey);
    void cancel(UUID paymentId, String reason);

    Page<?> search(Pageable pageable, Boolean includeDeleted); // 간단 검색(삭제 제외 기본)
}
