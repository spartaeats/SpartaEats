package com.sparta.sparta_eats.payment.infrastructure.persistence.spec;

import com.sparta.sparta_eats.payment.domain.entity.Payment;
import com.sparta.sparta_eats.payment.domain.entity.PaymentLog;
import com.sparta.sparta_eats.payment.domain.entity.PaymentIdempotency;
import org.springframework.data.jpa.domain.Specification;

public final class PaymentSpecs {
    private PaymentSpecs() {}

    /** deleted_at IS NULL */
    public static Specification<Payment> paymentNotDeleted() {
        return (root, q, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<PaymentLog> logNotDeleted() {
        return (root, q, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<PaymentIdempotency> idemNotDeleted() {
        return (root, q, cb) -> cb.isNull(root.get("deletedAt"));
    }
}
