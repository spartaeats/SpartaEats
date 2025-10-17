package com.sparta.sparta_eats.cart.infrastructure.repository;

import com.sparta.sparta_eats.cart.domain.entity.CartItemOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CartItemOptionRepository extends JpaRepository<CartItemOption, UUID> {

    // 특정 라인의 옵션들
    List<CartItemOption> findByCartItemId(UUID cartItemId);
}
