package com.sparta.sparta_eats.order.domain.entity;

import com.sparta.sparta_eats.global.entity.BaseEntity;
import com.sparta.sparta_eats.store.entity.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.UUID;

/**
 * 주문 아이템 엔티티 (order_items)
 *
 * - 주문에 속한 개별 상품 (예: 김치찌개 2개, 된장찌개 1개)
 * - 상품 정보, 수량, 옵션 합계 금액을 스냅샷으로 저장
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_order_items")
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /** 주문 (부모) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_item_order"))
    private Order order;

    /** 실제 상품 (스냅샷용 FK) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_item_product"))
    private Item item;

    /** 상품명 스냅샷 */
    @Column(name = "product_name", nullable = false, length = 255)
    private String itemName;

    /** 썸네일 이미지 URL (nullable) */
    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    /** 단가 */
    @Column(name = "unit_price", nullable = false)
    private BigInteger unitPrice = BigInteger.ZERO;

    /** 수량 */
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    /** 옵션 조합 해시 (옵션 중복 방지/분류 용도 - nullable) */
    @Column(name = "option_combo_hash", length = 64)
    private String optionComboHash;

    /** 옵션 총금액 */
    @Column(name = "option_total", nullable = false)
    private BigInteger optionTotal = BigInteger.ZERO;

    /** 한 줄(상품 + 옵션 × 수량) 총금액 */
    @Column(name = "line_price", nullable = false)
    private BigInteger linePrice = BigInteger.ZERO;

    // ===== Builder =====
    @Builder
    public OrderItem(Order order, Item item,
                     String itemName, String thumbnailUrl,
                     BigInteger unitPrice, Integer quantity,
                     BigInteger optionTotal, String optionComboHash) {

        this.order = order;
        this.item = item;
        this.itemName = itemName;
        this.thumbnailUrl = thumbnailUrl;

        this.unitPrice = nvl(unitPrice);
        this.quantity = (quantity != null && quantity > 0) ? quantity : 1;
        this.optionTotal = nvl(optionTotal);
        this.optionComboHash = optionComboHash;

        // 총합 계산
        recalcLinePrice();
    }

    /** 주문과의 연관을 명시적으로 연결 (가독성/의도 표현) */
    public void attachTo(Order order) {
        this.order = order;
    }

    /** 총합 재계산 (단가×수량 + 옵션) */
    public void recalcLinePrice() {
        this.linePrice = unitPrice
                .multiply(BigInteger.valueOf(quantity))
                .add(optionTotal);
    }

    /** 수량 변경 시 자동 재계산 */
    public void changeQuantity(int newQuantity) {
        if (newQuantity < 1) throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        this.quantity = newQuantity;
        recalcLinePrice();
    }

    /** 옵션 금액 변경 시 자동 재계산 */
    public void updateOptionTotal(BigInteger newOptionTotal) {
        this.optionTotal = nvl(newOptionTotal);
        recalcLinePrice();
    }

    private static BigInteger nvl(BigInteger v) {
        return (v != null) ? v : BigInteger.ZERO;
    }

    // ====== 유효성 검사 ======
    @PrePersist @PreUpdate
    private void validate() {
        if (order == null) throw new IllegalStateException("주문 정보가 없습니다.");
        if (item == null) throw new IllegalStateException("상품 정보가 없습니다.");
        if (itemName == null || itemName.isBlank()) throw new IllegalStateException("상품명이 없습니다.");
        if (unitPrice == null || unitPrice.compareTo(BigInteger.ZERO) < 0)
            throw new IllegalStateException("상품 단가가 잘못되었습니다.");
        if (quantity == null || quantity < 1)
            throw new IllegalStateException("수량은 1 이상이어야 합니다.");
    }

    // ===== equals/hashCode =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem)) return false;
        return id != null && id.equals(((OrderItem) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
