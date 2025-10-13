package com.sparta.sparta_eats.payment.domain.repository;

import com.sparta.sparta_eats.payment.domain.entity.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, UUID> {

    List<PaymentLog> findByPaymentIdOrderByCreatedAtDesc(UUID paymentId);

}
