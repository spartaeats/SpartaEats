package com.sparta.sparta_eats.order.domain.entity;

import com.sparta.sparta_eats.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"order"}) // 순환참조 방지
@Entity
@Table(name = "p_order_status_history")
public class OrderStatusHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Hibernate가 persist 시점에 UUID 생성
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /** 주문 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /** 변경 주체 ID (user.user_id, BIGINT) */
    @Column(name = "actor_id", nullable = false)
    private Long actorId;

    /** 변경 주체 역할(스냅샷) */
    @Column(name = "actor_role", nullable = false, length = 20)
    private String actorRole; // 스냅샷이라 Enum적용 안하고 String으로 처리

    /** 주문 상태 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    /** 취소 사유 (nullable) */
    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;

    @Builder
    private OrderStatusHistory(Order order,
                               Long actorId,
                               String actorRole,
                               OrderStatus status,
                               String cancelReason) {
        this.order = Objects.requireNonNull(order, "주문 정보는 비어 있을 수 없습니다.");
        this.actorId = Objects.requireNonNull(actorId, "변경 주체 ID는 비어 있을 수 없습니다.");
        this.actorRole = Objects.requireNonNull(actorRole, "변경 주체 역할은 비어 있을 수 없습니다.");
        this.status = Objects.requireNonNull(status, "주문 상태는 비어 있을 수 없습니다.");
        this.cancelReason = (cancelReason != null && !cancelReason.isBlank()) ? cancelReason : null;
    }

    // ====== 간단 헬퍼 ======
    public boolean isCanceled() {
        return this.status == OrderStatus.CANCELED;
    }

    /** 취소 상태일 때만 사유 설정 가능 */
    public void setCancelReason(String reason) {
        if (!isCanceled()) {
            throw new IllegalStateException("현재 상태가 취소(CANCELED)가 아니어서 취소 사유를 설정할 수 없습니다.");
        }
        this.cancelReason = (reason != null && !reason.isBlank()) ? reason : null;
    }

    // ====== 동등성: id가 하나라도 null이면 같지 않다고 본다 ======
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderStatusHistory other = (OrderStatusHistory) o;
        if (this.id == null || other.id == null) return false;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : System.identityHashCode(this);
    }

    // ====== enum  ======

    public enum OrderStatus {
        PLACED, CONFIRMED, COOKING, DELIVERY, COMPLETED, CANCELED
    }






}