package com.sparta.sparta_eats.order.domain.entity;

import com.sparta.sparta_eats.global.entity.BaseEntity;
import com.sparta.sparta_eats.store.domain.entity.Store;
import com.sparta.sparta_eats.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 주문 엔티티 (orders)
 * - user_id, store_id: FK
 * - 기본값: DELIVERY, PLACED, PENDING
 * - 주문번호(orderNo)는 생성 시 자동 부여
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    // ===== 연관관계 =====
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    // ===== 기본 주문 정보 =====
    @Column(name = "order_no", length = 30, nullable = false, unique = true)
    private String orderNo; // 자동 생성

    @Enumerated(EnumType.STRING)
    @Column(name = "fulfillment_type", nullable = false, length = 10)
    private FulfillmentType fulfillmentType = FulfillmentType.DELIVERY;

    // ===== 금액(스냅샷) =====
    @Column(name = "item_total", nullable = false)
    private BigInteger itemTotal = BigInteger.ZERO;

    @Column(name = "delivery_fee", nullable = false)
    private BigInteger deliveryFee = BigInteger.ZERO;

    @Column(name = "discount_total", nullable = false)
    private BigInteger discountTotal = BigInteger.ZERO;

    @Column(name = "total_amount", nullable = false)
    private BigInteger totalAmount = BigInteger.ZERO;

    // ===== 상태 =====
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private OrderStatus status = OrderStatus.PLACED;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 15)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    // ===== 요청 관련 =====
    @Column(name = "contact_phone", length = 13)
    private String contactPhone;

    @Column(name = "request_to_rider_code", length = 30)
    private String requestToRiderCode;

    @Column(name = "request_to_rider_text", length = 255)
    private String requestToRiderText;

    @Column(name = "request_to_store_text", length = 255)
    private String requestToStoreText;

    // ===== 옵션 관련 =====
    @Column(name = "no_cutlery", nullable = false)
    private Boolean noCutlery = false;

    @Column(name = "no_side_dish", nullable = false)
    private Boolean noSideDish = false;

    // ===== 주소 =====
    @Column(name = "addr_road", length = 255)
    private String addrRoad;

    @Column(name = "addr_detail", length = 255)
    private String addrDetail;

    @Column(name = "addr_postal_code", length = 10)
    private String addrPostalCode;

    @Column(name = "addr_lat")
    private Double addrLat;

    @Column(name = "addr_lng")
    private Double addrLng;

    // ===== 취소 관련 =====
    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;

    // ===== Builder =====
    @Builder
    public Order(User user, Store store, FulfillmentType fulfillmentType, String contactPhone) {
        this.user = user;
        this.store = store;
        this.fulfillmentType = (fulfillmentType != null) ? fulfillmentType : FulfillmentType.DELIVERY;
        this.contactPhone = contactPhone;
        this.orderNo = generateOrderNo();
    }

    // ===== 주문번호 자동 생성 =====
    private String generateOrderNo() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // ===== 주문 취소 =====
    public void cancel(String reason) {
        this.status = OrderStatus.CANCELED;
        this.canceledAt = LocalDateTime.now();
        this.cancelReason = reason;
    }

    // ===== ENUM =====
    public enum FulfillmentType { DELIVERY, PICKUP }

    public enum OrderStatus { PLACED, CONFIRMED, COOKING, DELIVERY, COMPLETED, CANCELED }

    public enum PaymentStatus { PENDING, AUTHORIZED, PAID, REFUNDED, FAILED }

    // ===== 중복 생기지 않도록 Lombok의 @EqualsAndHashCode를 쓰면 위험(엔티티끼리 무한순환생길수있다함)=====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        return id != null && id.equals(((Order) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // ===== 유효성 검사 =====
    @PrePersist
    private void validate() {
        if (user == null) throw new IllegalStateException("주문자 정보가 없습니다.");
        if (store == null) throw new IllegalStateException("매장 정보가 없습니다.");
        if (orderNo == null) this.orderNo = generateOrderNo();
    }



    /**
     * BigDecimal로 전환하기 위해 메서드 만들었습니다. (한빈)
     */
    public java.math.BigDecimal getTotalAmountDecimal() {
        return (this.totalAmount != null)
                ? new java.math.BigDecimal(this.totalAmount)  // scale 0
                : java.math.BigDecimal.ZERO;
    }

    // 결제 확정(승인) 시
    public void markPaymentPaid() {
        this.paymentStatus = PaymentStatus.PAID;
        if (this.status == OrderStatus.PLACED) {
            this.status = OrderStatus.CONFIRMED; // 팀 룰: 결제완료 → 접수(또는 원하는 상태로)
        }
    }

    // 결제 취소 시
    public void markPaymentCanceled(String reason) {
        this.paymentStatus = PaymentStatus.REFUNDED; // 또는 FAILED, 팀 룰에 맞게
        this.cancel(reason); // 이미 존재하는 cancel(reason) 재사용
    }


    public OrderStatusHistory toStatusHistory(Long actorId, String actorRole,
                                              OrderStatus newStatus, String cancelReason) {
        return OrderStatusHistory.builder()
                .order(this)
                .actorId(actorId)
                .actorRole(actorRole)
                .status(OrderStatusHistory.OrderStatus.valueOf(newStatus.name())) // ← 변환
                .cancelReason(cancelReason)
                .build();
    }




}
