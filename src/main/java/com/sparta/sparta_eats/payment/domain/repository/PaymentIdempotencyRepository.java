package com.sparta.sparta_eats.payment.domain.repository;

import com.sparta.sparta_eats.payment.domain.entity.PaymentIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PaymentIdempotencyRepository extends JpaRepository<PaymentIdempotency, String>, JpaSpecificationExecutor<PaymentIdempotency> {


}
