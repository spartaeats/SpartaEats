package com.sparta.sparta_eats.cart.domain.repository;

import com.sparta.sparta_eats.cart.domain.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
  // 기본 CRUD는 JpaRepository가 제공
  // 필요한 커스텀 메서드는 팀원들이 추가
}