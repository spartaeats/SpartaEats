package com.sparta.sparta_eats.cart.domain.entity;

import com.sparta.sparta_eats.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cart_item_options")
public class CartItemOption extends BaseEntity {

  @Id
  @UuidGenerator
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "cart_item_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_cart_item_opts_item"))
  private CartItem cartItem;

  @Column(name = "item_opt_id", nullable = false)
  private UUID itemOptId;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;
}