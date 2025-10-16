package com.sparta.sparta_eats.payment.domain.repository;

import com.sparta.sparta_eats.payment.domain.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {

    Optional<Payment> findByIdempotencyKey(String key);

    // @Where 무시하고 원테이블 전부 (soft-deleted 포함)
    @Query(
            value = "select * from p_payment order by created_at desc",
            countQuery = "select count(*) from p_payment",
            nativeQuery = true
    )
    Page<Payment> findAllIncludingDeleted(Pageable pageable);
}
