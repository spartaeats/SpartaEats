package com.sparta.sparta_eats.cart.domain.entity;

import com.sparta.sparta_eats.global.entity.BaseEntity;
import com.sparta.sparta_eats.store.domain.entity.Store;
import com.sparta.sparta_eats.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Cart (장바구니)
 * - 한 사용자가 한 매장의 상품만 담을 수 있음
 * - 장바구니에는 여러 개의 CartItem이 연결됨
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_carts")
public class Cart extends BaseEntity {

    /** 기본키: UUID */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /** 사용자 (N:1) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /** 매장 (N:1) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    /** 장바구니 아이템들 (1:N) */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    /** 배송 주소 ID (선택적) */
    @Column(name = "address_id")
    private UUID addressId;

    /** Builder 생성자 — 필요한 필드만 초기화 */
    @Builder
    public Cart(User user, Store store, UUID addressId) {
        this.user = user;
        this.store = store;
        this.addressId = addressId;
    }

    /** 아이템 추가 (연관관계 관리) */
    public void addItem(CartItem item) {
        if (item == null) throw new IllegalArgumentException("item must not be null");
        items.add(item);
        item.setCart(this);
    }

    /** 장바구니 안에 담긴 상품 수 */
    public int getTotalItemCount() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    /** 주소 ID 설정 */
    public void setAddressId(UUID addressId) {
        this.addressId = addressId;
    }

}
