package com.sparta.sparta_eats.order.infrastructure.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.sparta_eats.order.domain.entity.Order;
import com.sparta.sparta_eats.order.domain.repository.OrderSearchRepository;
import com.sparta.sparta_eats.order.presentation.dto.request.OrderSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.sparta.sparta_eats.order.domain.entity.QOrder.order;
import static com.sparta.sparta_eats.order.domain.entity.QOrderItem.orderItem;
import static com.sparta.sparta_eats.store.domain.entity.QStore.store;

@RequiredArgsConstructor
public class OrderSearchRepositoryImpl implements OrderSearchRepository {
    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Order> search(OrderSearchCondition condition, Pageable pageable) {
        List<Order> content = queryFactory
                .selectFrom(order)
                .join(order.store, store).fetchJoin()
                .where(addressContains(condition.address()),
                        betweenDate(condition.monthFrom(), condition.monthTo()),
                        orderStatusEq(condition.orderOutcome()),
                        textSearch(condition.q()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(order.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(order.count())
                .from(order)
                .join(order.store, store)
                .where(
                        addressContains(condition.address()),
                        betweenDate(condition.monthFrom(), condition.monthTo()),
                        orderStatusEq(condition.orderOutcome()),
                        textSearch(condition.q())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private BooleanExpression addressContains(String address) {
        if(StringUtils.hasText(address))
            return order.addrRoad.containsIgnoreCase(address);
        else
            return null;
    }

    private BooleanExpression orderStatusEq(String orderOutcome) {
        if (!StringUtils.hasText(orderOutcome)) {
            return null;
        }
        try {
            // "COMPLETED"와 같은 문자열을 OrderStatus Enum 타입으로 변환
            Order.OrderStatus status = Order.OrderStatus.valueOf(orderOutcome.toUpperCase());
            return order.status.eq(status);
        } catch (IllegalArgumentException e) {
            // 잘못된 orderOutcome 값이 들어올 경우 무시
            return null;
        }
    }

    /**
     * 텍스트 검색 (가게명 또는 아이템명)
     * 단방향 관계이므로 아이템명 검색은 서브쿼리를 사용
     */
    private BooleanExpression textSearch(String q) {
        if (!StringUtils.hasText(q)) {
            return null;
        }

        // 1. 가게명에 q가 포함되는 조건
        BooleanExpression storeNameMatches = store.name.containsIgnoreCase(q);

        // 2. 이 주문(order)에 연결된 OrderItem 중 이름에 q가 포함된 것이 '존재하는지' 확인하는 서브쿼리
        BooleanExpression itemExists = JPAExpressions
                .selectFrom(orderItem)
                .where(
                        orderItem.order.id.eq(order.id), // 서브쿼리와 메인쿼리의 order를 연결
                        orderItem.itemName.containsIgnoreCase(q)
                ).exists();

        // 가게명이 일치하거나, 또는 조건을 만족하는 아이템이 존재하면 참
        return storeNameMatches.or(itemExists);
    }

    /**
     * 조회 기간 (YYYY-MM)
     */
    private BooleanExpression betweenDate(String monthFrom, String monthTo) {
        // 이 메소드는 Order 엔티티 구조와 직접적인 관련이 없으므로 기존 로직 유지
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        YearMonth from = StringUtils.hasText(monthFrom) ? YearMonth.parse(monthFrom, formatter) : YearMonth.now();
        YearMonth to = StringUtils.hasText(monthTo) ? YearMonth.parse(monthTo, formatter) : YearMonth.now();

        LocalDateTime startDateTime = from.atDay(1).atStartOfDay();
        LocalDateTime endDateTime = to.atEndOfMonth().atTime(23, 59, 59);

        return order.createdAt.between(startDateTime, endDateTime);
    }
}
