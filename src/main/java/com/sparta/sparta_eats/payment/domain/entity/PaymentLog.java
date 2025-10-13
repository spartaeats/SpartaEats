package com.sparta.sparta_eats.payment.domain.entity;


import com.sparta.sparta_eats.payment.domain.entity.base.PaymentSoftDeletable;
import com.sparta.sparta_eats.payment.domain.model.PaymentEventType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "p_payment_log")
public class PaymentLog extends PaymentSoftDeletable {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "payment_log_id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "payment_id", nullable = false, columnDefinition = "uuid")
    private UUID paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 30)
    private PaymentEventType eventType;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Lob
    @Column(name = "request_body")
    private String requestBody;

    @Lob
    @Column(name = "response_body")
    private String responseBody;

}
