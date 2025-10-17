package com.sparta.sparta_eats.order.domain.repository;

import com.sparta.sparta_eats.order.domain.entity.Order;
import com.sparta.sparta_eats.order.presentation.dto.request.OrderSearchCondition;
import com.sparta.sparta_eats.user.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderSearchRepository {
    Page<Order> search(User user, OrderSearchCondition condition, Pageable pageable);
}
