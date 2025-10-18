package com.sparta.sparta_eats.cart.infrastructure.repository;

import com.sparta.sparta_eats.cart.domain.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

}
