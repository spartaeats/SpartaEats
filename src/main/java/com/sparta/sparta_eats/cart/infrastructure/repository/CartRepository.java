package com.sparta.sparta_eats.cart.infrastructure.repository;

import com.sparta.sparta_eats.cart.domain.entity.Cart;
import com.sparta.sparta_eats.user.domain.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {

    // 사용자 현재 카트(매장 무관) 조회
    Optional<Cart> findByUser(User user);

    // 사용자 + 매장 기준 카트 조회
    Optional<Cart> findByUserAndStoreId(User user, UUID storeId);

    // 동일 매장 카트 보유 여부
    boolean existsByUserAndStoreId(User user, UUID storeId);

    // 카트 + 아이템(+옵션)까지 한 번에 스냅샷 조회 (응답용)
    @EntityGraph(attributePaths = {"items", "items.options"})
    Optional<Cart> findWithItemsByIdAndUser(UUID id, User user);
    
    // 사용자로 카트 + 아이템(+옵션)까지 한 번에 조회
    @EntityGraph(attributePaths = {"items", "items.options"})
    Optional<Cart> findWithItemsByUser(User user);

}

