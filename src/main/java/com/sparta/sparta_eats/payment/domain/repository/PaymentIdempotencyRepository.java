package com.sparta.sparta_eats.payment.domain.repository;

import com.sparta.sparta_eats.payment.domain.entity.PaymentIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentIdempotencyRepository extends JpaRepository<PaymentIdempotency, String> {


}
