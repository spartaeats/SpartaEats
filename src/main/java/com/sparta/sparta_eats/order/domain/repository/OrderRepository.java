package com.sparta.sparta_eats.order.domain.repository;

import com.sparta.sparta_eats.order.domain.entity.Order;
import com.sparta.sparta_eats.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, OrderSearchRepository {
    List<Order> findAllByUser(User user);
}
