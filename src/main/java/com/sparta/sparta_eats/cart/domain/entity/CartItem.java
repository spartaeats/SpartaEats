package com.sparta.sparta_eats.cart.domain.entity;

import com.sparta.sparta_eats.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "cart_items",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_cart_option_combo", columnNames = {"cart_id", "option_combo_hash"})
    }
)
public class CartItem extends BaseEntity {

  @Id
  @UuidGenerator
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "cart_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_cart_items_cart"))
  private Cart cart;

  @Column(name = "item_id", nullable = false)
  private UUID itemId;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @Column(name = "option_combo_hash", length = 64)
  private String optionComboHash;

  @OneToMany(mappedBy = "cartItem", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<CartItemOption> options = new HashSet<>();

  // 간단한 수량 증가 메서드
  public void increaseQuantity(int amount) {
    this.quantity += amount;
  }
}