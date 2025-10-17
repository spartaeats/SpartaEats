package com.sparta.sparta_eats.cart.domain.entity;

import com.sparta.sparta_eats.global.entity.BaseEntity;
import com.sparta.sparta_eats.item.domain.entity.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * CartItem (장바구니에 담긴 개별 상품)
 * - Cart 1개 안에 여러 CartItem 존재 (N:1)
 * - Item(상품)과 연결됨 (N:1)
 * - 옵션 정보는 CartItemOption으로 관리 (1:N)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_cart_items")
public class CartItem extends BaseEntity {

    /** PK: UUID */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /** 장바구니(FK) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    /** 실제 상품 (FK) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /** 수량 */
    @Column(nullable = false)
    private int quantity;

    /** 상품 단가 */
    @Column(name = "item_price", nullable = false)
    private BigInteger itemPrice;

    /** 옵션 총합 금액 */
    @Column(name = "option_total_price", nullable = false)
    private BigInteger optionTotalPrice = BigInteger.ZERO;

    /** 상품 + 옵션 총합 */
    @Column(name = "total_price", nullable = false)
    private BigInteger totalPrice;

    /** 옵션들 (1:N) */
    @OneToMany(mappedBy = "cartItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemOption> options = new ArrayList<>();

    /** 생성자 Builder */
    @Builder
    public CartItem(Item item, int quantity, BigInteger itemPrice) {
        this.item = item;
        this.quantity = quantity;
        this.itemPrice = itemPrice;
        this.totalPrice = itemPrice.multiply(BigInteger.valueOf(quantity));
    }

    public void setCart(Cart cart) {
        this.cart = cart;
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
}
