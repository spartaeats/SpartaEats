package com.sparta.sparta_eats.order.infrastructure.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.sparta_eats.order.domain.entity.Order;
import com.sparta.sparta_eats.order.domain.repository.OrderSearchRepository;
import com.sparta.sparta_eats.order.presentation.dto.request.OrderSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class OrderSearchRepositoryImpl implements OrderSearchRepository {
    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Order> search(OrderSearchCondition condition, Pageable pageable) {
        return null;
    }
}
