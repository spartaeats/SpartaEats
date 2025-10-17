package com.sparta.sparta_eats.payment.domain.repository;

import com.sparta.sparta_eats.payment.domain.entity.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, UUID>, JpaSpecificationExecutor<PaymentLog> {

    List<PaymentLog> findByPaymentIdOrderByCreatedAtDesc(UUID paymentId);

}
