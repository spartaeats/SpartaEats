package com.sparta.sparta_eats.order.domain.entity;

import com.sparta.sparta_eats.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)   // JPA 프록시용 기본 생성자
@ToString(exclude = {"orderItem"})                   // 순환참조/로그 폭탄 방지
@Entity
@Table(
        name = "p_order_item_options",
        indexes = {
                @Index(name = "idx_oio_order_item_id", columnList = "order_item_id"),
                @Index(name = "idx_oio_item_option_id", columnList = "item_option_id")
        }
)
public class OrderItemOption extends BaseEntity {

    // ===== 식별자 =====
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)  // Hibernate가 persist 시점에 UUID 생성
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    // ===== 연관관계 =====
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    // 원본 옵션 식별자(스냅샷) — 실제 연관 대신 값 보관
    @Column(name = "item_option_id", nullable = false)
    private UUID itemOptionId;

    // ===== 스냅샷 필드 =====
    @Column(name = "option_name", length = 100, nullable = false)
    private String optionName;

    /** 추가금(원 단위, BIGINT) */
    @Column(name = "add_price", nullable = false)
    private BigDecimal addPrice;

    /** 옵션 수량 */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // ===== 생성자/빌더 =====
    @Builder(toBuilder = true)
    private OrderItemOption(
            OrderItem orderItem,
            UUID itemOptionId,
            String optionName,
            BigDecimal addPrice,
            Integer quantity
    ) {
        this.orderItem = orderItem;                 // 양방향 연결은 setOrderItem로 맞추는 것을 권장
        this.itemOptionId = Objects.requireNonNull(itemOptionId, "itemOptionId must not be null");
        this.optionName = Objects.requireNonNull(optionName, "optionName must not be null");
        this.addPrice = (addPrice != null) ? addPrice : BigDecimal.ZERO;
        this.quantity = (quantity != null) ? quantity : 1;
    }

    /**
     * 원본 ItemOption 엔티티에서 스냅샷을 뜨는 경우에 사용하는 정적 팩토리
     */
    public static OrderItemOption ofSnapshot(OrderItem orderItem,
                                             UUID itemOptionId,
                                             String optionName,
                                             BigDecimal addPrice,
                                             int quantity) {
        return OrderItemOption.builder()
                .orderItem(orderItem)
                .itemOptionId(itemOptionId)
                .optionName(optionName)
                .addPrice(addPrice)
                .quantity(quantity)
                .build();
    }

    // ===== 비즈니스/편의 메서드 =====
    /** 양쪽 참조가 동시에 일관되기위한..? */
    void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    /** 옵션 총 추가금 = add_price × quantity */
    public BigDecimal getTotalAddPrice() {
        return addPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void changeQuantity(int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("quantity must be >= 1");
        this.quantity = quantity;
    }

    public void increaseQuantity(int delta) {
        if (delta < 1) return;
        this.quantity += delta;
    }

    public void decreaseQuantity(int delta) {
        if (delta < 1) return;
        int next = this.quantity - delta;
        if (next < 1) throw new IllegalArgumentException("quantity must be >= 1");
        this.quantity = next;
    }

    /** 스냅샷 업데이트(메뉴명/가격 정책 변경 반영 등) */
    public void updateSnapshot(String optionName, BigDecimal addPrice) {
        if (optionName != null && !optionName.isBlank()) this.optionName = optionName;
        if (addPrice != null && addPrice.longValue() >= 0) this.addPrice = addPrice;
    }

    // ===== equals/hashCode 수동 구현 (Hibernate UUID 자동생성시 수동구현이 좋다함) =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // 프록시 대비: 정확한 엔티티 클래스 비교
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemOption other = (OrderItemOption) o;
        // id가 하나라도 null이면 동등 아님(신규 엔티티 혼동 방지)
        if (this.id == null || other.id == null) return false;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        // id가 없을 때는 객체 아이덴티티 기반으로 임시 해시 제공
        return (id != null) ? id.hashCode() : System.identityHashCode(this);
    }
}
