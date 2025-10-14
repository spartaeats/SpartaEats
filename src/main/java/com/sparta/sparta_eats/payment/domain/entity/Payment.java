package com.sparta.sparta_eats.payment.domain.entity;


import com.sparta.sparta_eats.payment.domain.entity.base.PaymentSoftDeletable;
import com.sparta.sparta_eats.payment.domain.model.PaymentMethod;
import com.sparta.sparta_eats.payment.domain.model.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "p_payment",
        indexes = {
                @Index(name = "idx_payment_order_id", columnList = "order_id"),
                @Index(name = "idx_payment_status", columnList = "status"),
                @Index(name = "idx_payment_created_at", columnList = "created_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_payment_idempotency_key", columnNames = {"idempotency_key"})
        }
)
@SQLDelete(sql = "UPDATE p_payment SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Payment extends PaymentSoftDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID orderId;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, length = 20)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PaymentStatus status;

    @Column(name = "idempotency_key", length = 100, unique = true)
    private String idempotencyKey;

    @Column(name = "pg_payment_key", length = 100)
    private String pgPaymentKey;

    @Column(name = "note", length = 500)
    private String note;

    /* ---------- 도메인 메서드: 상태 전이 규칙 ---------- */

    /** 결제 생성 직후 요청 상태로 설정 (멱등키 포함) */
    public void markRequested(String idempotencyKey) {
        ensureStatusIsNullOr(PaymentStatus.PENDING); // 생성 직후 또는 이미 PENDING
        this.status = PaymentStatus.PENDING;
        this.idempotencyKey = idempotencyKey;
    }

    /** 승인(확정) — PENDING → CONFIRMED 만 허용 */
    public void confirm(String pgPaymentKey) {
        ensureCurrent(PaymentStatus.PENDING, "승인은 PENDING 상태에서만 가능합니다.");
        this.status = PaymentStatus.CONFIRMED;
        this.pgPaymentKey = pgPaymentKey;
    }

    /** 취소 — 생성 후 5분 이내 & PENDING 에서만 허용 */
    public void cancel(String reason) {
        ensureCurrent(PaymentStatus.PENDING, "취소는 PENDING 상태에서만 가능합니다.");
        ensureCancelableWithinMinutes(5, "결제 생성 후 5분이 지나 취소할 수 없습니다.");
        this.status = PaymentStatus.CANCELED;
        this.note = reason;
    }

    /** 실패 — PENDING → FAILED 만 허용 */
    public void fail(String reason) {
        ensureCurrent(PaymentStatus.PENDING, "실패 전환은 PENDING 상태에서만 가능합니다.");
        this.status = PaymentStatus.FAILED;
        this.note = reason;
    }

    /* ---------- 내부 검증 도우미 ---------- */

    private void ensureCurrent(PaymentStatus expected, String message) {
        if (this.status != expected) {
            throw new IllegalStateException(message + " (현재: " + this.status + ")");
        }
    }

    private void ensureStatusIsNullOr(PaymentStatus expectedIfPresent) {
        if (this.status != null && this.status != expectedIfPresent) {
            throw new IllegalStateException("잘못된 초기 상태 전이입니다. (현재: " + this.status + ")");
        }
    }

    private void ensureCancelableWithinMinutes(int minutes, String message) {
        // BaseEntity.createdAt 사용 (Auditing으로 자동 채워짐)
        if (getCreatedAt() == null) return; // 테스트 초기 케이스 방어
        var deadline = getCreatedAt().plusMinutes(minutes);
        if (LocalDateTime.now().isAfter(deadline)) {
            throw new IllegalStateException(message);
        }
    }
}
