package com.sparta.sparta_eats.order.domain.entity;

import com.sparta.sparta_eats.address.domain.dto.AddressSupplyDto;
import com.sparta.sparta_eats.address.domain.entity.Coordinate;
import com.sparta.sparta_eats.global.entity.BaseEntity;
import com.sparta.sparta_eats.order.domain.dto.OrderSnapshotDto;
import com.sparta.sparta_eats.store.entity.Store;
import com.sparta.sparta_eats.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private BigDecimal itemTotal = BigDecimal.ZERO;

    @Column(name = "delivery_fee", nullable = false)
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @Column(name = "discount_total", nullable = false)
    private BigDecimal discountTotal = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

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

    // discuss riderCode는 주소의 memo로 저장
    @Column(name = "request_to_rider_code", length = 30)
    private String requestToRiderCode;

    // discuss riderText는 주문시 받는 사항
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

    // TODO 우편번호는 필요 없다고 판단 우선 제외 후 상의
    @Column(name = "addr_postal_code", length = 10)
    private String addrPostalCode;

    @Embedded
    private Coordinate coordinate;

    // ===== 취소 관련 =====
    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;

    // ===== Builder =====
    @Builder
    public Order(User user, Store store, FulfillmentType fulfillmentType, String contactPhone, Boolean noCutlery, Boolean noSideDish, String memoToOwner, String memoToRider) {
        this.user = user;
        this.store = store;
        this.fulfillmentType = (fulfillmentType != null) ? fulfillmentType : FulfillmentType.DELIVERY;
        this.contactPhone = contactPhone;
        this.orderNo = generateOrderNo();
        this.requestToRiderText = memoToRider;
        this.requestToStoreText = memoToOwner;
        this.noCutlery = noCutlery;
        this.noSideDish = noSideDish;
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
    public enum FulfillmentType {DELIVERY, PICKUP}

    public enum OrderStatus {PLACED, CONFIRMED, COOKING, DELIVERY, COMPLETED, CANCELED}

    public enum PaymentStatus {PENDING, AUTHORIZED, PAID, REFUNDED, FAILED}

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

    // ===== Entity 할당 메서드 =====
    public void assignAddress(AddressSupplyDto supplyDto) {
        addrRoad = supplyDto.addrRoad();
        addrDetail = supplyDto.addrDetail();
        coordinate = supplyDto.coordinate();
        requestToRiderCode = supplyDto.memo();
    }

    // ===== 스냅샷 할당 메서드 =====
    public void assignItemSnapshot(OrderSnapshotDto snapshotDto) {
        itemTotal = snapshotDto.itemTotal();
        deliveryFee = snapshotDto.deliveryFee();
        discountTotal = snapshotDto.discountTotal();
        totalAmount = snapshotDto.totalAmount();
    }
}
