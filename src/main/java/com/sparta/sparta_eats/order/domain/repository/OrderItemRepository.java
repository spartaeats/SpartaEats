package com.sparta.sparta_eats.order.domain.repository;

import com.sparta.sparta_eats.order.domain.entity.Order;
import com.sparta.sparta_eats.order.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    List<OrderItem> findAllByOrder(Order order);
    List<OrderItem> findAllByOrderIn(List<Order> orderList);

    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.item WHERE oi.order = :order")
    List<OrderItem> findAllByOrderWithItem(@Param("order") Order order);
}
