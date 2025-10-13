package com.sparta.sparta_eats.payment.domain.entity;


import com.sparta.sparta_eats.payment.domain.entity.base.PaymentSoftDeletable;
import com.sparta.sparta_eats.payment.domain.model.PaymentMethod;
import com.sparta.sparta_eats.payment.domain.model.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_payment")
@Getter
@Setter
public class Payment extends PaymentSoftDeletable {

    @Id
    @Column(name = "payment_id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "order_id", nullable = false, columnDefinition = "uuid")
    private UUID orderId;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, length = 30)
    private PaymentMethod method; // CARD only (확장 고려해도 enum OK)

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status; // PENDING/CONFIRMED/CANCELED/FAILED

    @Column(name = "payment_key", length = 128)   // PG 결제키(확정 시)
    private String paymentKey;

    @Column(name = "idempotency_key", length = 128, unique = true)
    private String idempotencyKey;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "is_hidden", nullable = false)
    private boolean hidden = false;               // 숨김 처리 (삭제와 별개)



}
