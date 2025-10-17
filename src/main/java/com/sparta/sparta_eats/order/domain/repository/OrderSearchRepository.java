package com.sparta.sparta_eats.order.domain.repository;

import com.sparta.sparta_eats.order.domain.entity.Order;
import com.sparta.sparta_eats.order.presentation.dto.request.OrderSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderSearchRepository {
    Page<Order> search(OrderSearchCondition condition, Pageable pageable);
}
