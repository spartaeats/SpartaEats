package com.sparta.sparta_eats.order.domain.repository;

import com.sparta.sparta_eats.order.domain.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, UUID> {

  /**
   * 주문 ID로 상태 이력 조회 (생성 시간 오름차순)
   * @param orderId 주문 ID
   * @return 상태 이력 리스트 (시간순)
   */
  List<OrderStatusHistory> findByOrder_IdOrderByCreatedAtAsc(UUID orderId);
}