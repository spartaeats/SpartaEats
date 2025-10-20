package com.sparta.sparta_eats.cart.infrastructure.repository;

import com.sparta.sparta_eats.cart.domain.entity.CartItem;
import com.sparta.sparta_eats.cart.domain.entity.CartItemOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemOptionRepository extends JpaRepository<CartItemOption, UUID> {

    // 특정 라인의 옵션들
    List<CartItemOption> findByCartItemId(UUID cartItemId);
    
    // CartItem 엔티티로 옵션들 조회
    List<CartItemOption> findByCartItem(CartItem cartItem);
    
    // JOIN FETCH로 ItemOption을 함께 조회 (LazyInitializationException 방지)
    @Query("SELECT cio FROM CartItemOption cio JOIN FETCH cio.itemOption WHERE cio.cartItem = :cartItem")
    List<CartItemOption> findByCartItemWithItemOption(@Param("cartItem") CartItem cartItem);
    
    // ID로 CartItemOption을 조회하면서 ItemOption도 함께 조회 (LazyInitializationException 방지)
    @Query("SELECT cio FROM CartItemOption cio JOIN FETCH cio.itemOption WHERE cio.id = :id")
    Optional<CartItemOption> findByIdWithItemOption(@Param("id") UUID id);
}
