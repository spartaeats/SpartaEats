package com.sparta.sparta_eats.payment.domain.entity;

import com.sparta.sparta_eats.payment.domain.entity.base.PaymentSoftDeletable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;




@SQLDelete(sql = "UPDATE p_payment_idempotency SET deleted_at = now() WHERE idempotency_key = ?")
@Where(clause = "deleted_at IS NULL")
@Getter
@Setter
@Entity
@Table(name = "p_payment_idempotency")
public class PaymentIdempotency extends PaymentSoftDeletable {

    @Id
    @Column(name = "idempotency_key", length = 128)
    private String idempotencyKey;

    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "payment_id", columnDefinition = "uuid")
    private UUID paymentId;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;


}
