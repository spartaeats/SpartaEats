package com.sparta.sparta_eats.order.domain.repository;

import com.sparta.sparta_eats.order.domain.entity.OrderItem;
import com.sparta.sparta_eats.order.domain.entity.OrderItemOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemOptionRepository extends JpaRepository<OrderItemOption, UUID> {
    List<OrderItemOption> findAllByOrderItemIn(List<OrderItem> orderItemList);
}
