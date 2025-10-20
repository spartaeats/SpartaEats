package com.sparta.sparta_eats.cart.infrastructure.repository;

import com.sparta.sparta_eats.cart.domain.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    // 카트 내 모든 라인
    List<CartItem> findByCartId(UUID cartId);

    // 합치기(merge)용: 동일 옵션 조합 라인 찾기
    Optional<CartItem> findByCartIdAndOptionComboHash(UUID cartId, String optionComboHash);

    // UNIQUE (cart_id, option_combo_hash) 적용 시 빠른 존재 확인
    boolean existsByCartIdAndOptionComboHash(UUID cartId, String optionComboHash);
}
