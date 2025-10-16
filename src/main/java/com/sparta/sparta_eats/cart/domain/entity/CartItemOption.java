package com.sparta.sparta_eats.cart.domain.entity;

import com.sparta.sparta_eats.global.entity.BaseEntity;
import com.sparta.sparta_eats.store.domain.entity.ItemOption;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * p_cart_item_options (장바구니 상품옵션)
 * 장바구니에 담긴 상품의 옵션정보포함
 * ex) 치즈추가, 곱빼기, 음료수 등
 * - id             : UUID PK
 * - cart_item_id   : UUID NOT NULL   (N:1, ON DELETE CASCADE)
 * - item_option_id : UUID NOT NULL   (N:1, ON DELETE RESTRICT)
 * - quantity       : INT NOT NULL CHECK (quantity >= 1)
 * - created_at / updated_at : BaseEntity
 * -
 * - 연관관계 연결은 attachTo(CartItem)로만 수행 (명시적)
 */


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_cart_item_options")
public class CartItemOption extends BaseEntity {

    /** PK: UUID */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /** CartItem (N:1) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "cart_item_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_cart_item_option_cart_item")
    )
    private CartItem cartItem;

    /** 실제 ItemOption 참조 (N:1) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "item_option_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_cart_item_option_item_option")
    )
    private ItemOption itemOption;

    /** 옵션 수량 (>= 1) */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /* ===== 생성자 기반 Builder (연관은 attachTo로 연결) ===== */
    @Builder
    public CartItemOption(ItemOption itemOption, Integer quantity) {
        this.itemOption = itemOption;
        this.quantity = (quantity != null && quantity > 0) ? quantity : 1;
    }

    /* ===== 연관관계 연결 헬퍼 ===== */
    public void attachTo(CartItem cartItem) {
        this.cartItem = cartItem;
    }

    /* ===== 비즈니스 메서드 ===== */

    public void increaseQuantity(int amount) {
        if (amount <= 0) return;
        this.quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        if (amount <= 0) return;
        this.quantity = Math.max(1, this.quantity - amount);
    }

    /* ===== 데이터 무결성 ===== */
    @PrePersist     //Insert 전 자동호출됨
    @PreUpdate      //Update 전 자동호출됨
    private void validate() {
        if (cartItem == null) throw new IllegalStateException("장바구니 상품이 없습니다.");
        if (itemOption == null) throw new IllegalStateException("상품 옵션이 연결되지 않았습니다.");
        if (quantity == null || quantity < 1) throw new IllegalStateException("옵션수량은 1개 이상이여야 합니다.");
    }
}
