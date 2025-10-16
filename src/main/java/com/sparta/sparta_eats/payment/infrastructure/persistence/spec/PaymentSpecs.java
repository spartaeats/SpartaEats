package com.sparta.sparta_eats.payment.infrastructure.persistence.spec;

import com.sparta.sparta_eats.payment.domain.entity.Payment;
import com.sparta.sparta_eats.payment.domain.entity.PaymentLog;
import com.sparta.sparta_eats.payment.domain.entity.PaymentIdempotency;
import com.sparta.sparta_eats.payment.domain.model.PaymentMethod;
import com.sparta.sparta_eats.payment.domain.model.PaymentStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class PaymentSpecs {
    private PaymentSpecs() {}

    public static Specification<Payment> hasStatus(PaymentStatus status) {
        return (root, q, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Payment> hasMethod(PaymentMethod method) {
        return (root, q, cb) -> method == null ? cb.conjunction() : cb.equal(root.get("method"), method);
    }

    public static Specification<Payment> hasUserId(String userId) {
        return (root, q, cb) -> (userId == null || userId.isBlank()) ? cb.conjunction() : cb.equal(root.get("userId"), userId);
    }

    public static Specification<Payment> createdBetween(LocalDateTime start, LocalDateTime end) {
        return (root, q, cb) -> {
            if (start != null && end != null) return cb.between(root.get("createdAt"), start, end);
            if (start != null) return cb.greaterThanOrEqualTo(root.get("createdAt"), start);
            if (end != null) return cb.lessThanOrEqualTo(root.get("createdAt"), end);
            return cb.conjunction();
        };
    }


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
