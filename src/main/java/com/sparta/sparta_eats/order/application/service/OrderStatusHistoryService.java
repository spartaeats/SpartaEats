package com.sparta.sparta_eats.order.application.service;

import com.sparta.sparta_eats.global.domain.exception.NotFoundException;
import com.sparta.sparta_eats.order.domain.entity.Order;
import com.sparta.sparta_eats.order.domain.entity.OrderStatusHistory;
import com.sparta.sparta_eats.order.domain.repository.OrderRepository;
import com.sparta.sparta_eats.order.domain.repository.OrderStatusHistoryRepository;
import com.sparta.sparta_eats.order.presentation.dto.response.OrderStatusHistoryResponse;
import com.sparta.sparta_eats.order.presentation.dto.response.StatusHistoryDto;
import com.sparta.sparta_eats.store.entity.Store;
import com.sparta.sparta_eats.user.domain.entity.User;
import com.sparta.sparta_eats.user.domain.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 주문 상태 이력 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderStatusHistoryService {

  private final OrderStatusHistoryRepository orderStatusHistoryRepository;
  private final OrderRepository orderRepository;

  /**
   * 주문 상태 이력 조회
   *
   * @param orderId 주문 ID
   * @param user 요청한 사용자
   * @return 주문 상태 이력 응답
   */
  public OrderStatusHistoryResponse getOrderStatusHistory(UUID orderId, User user) {
    // 1. 주문 존재 확인
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다."));

    // 2. 권한 검증
    validateAccessPermission(order, user);

    // 3. 이력 조회 (시간순 오름차순)
    List<OrderStatusHistory> histories = orderStatusHistoryRepository
        .findByOrder_IdOrderByCreatedAtAsc(orderId);

    // 4. DTO 변환
    List<StatusHistoryDto> historyDtos = histories.stream()
        .map(StatusHistoryDto::from)
        .collect(Collectors.toList());

    // 5. 응답 생성
    return OrderStatusHistoryResponse.of(orderId, historyDtos);
  }

  /**
   * 접근 권한 검증
   * - OWNER: 자신의 매장 주문만 조회 가능
   * - MANAGER/MASTER: 모든 주문 조회 가능
   * - CUSTOMER: 자신의 주문만 조회 가능
   *
   * @param order 주문
   * @param user 사용자
   */
  private void validateAccessPermission(Order order, User user) {
    UserRole role = user.getRole();

    // MANAGER, MASTER는 모든 주문 조회 가능
    if (role == UserRole.MANAGER || role == UserRole.MASTER) {
      return;
    }

    // OWNER는 자신의 매장 주문만 조회 가능
    if (role == UserRole.OWNER) {
      Store store = order.getStore();
      String storeOwnerId = store.getOwner().getUserId();

      if (!storeOwnerId.equals(user.getUserId())) {
        throw new NotFoundException("해당 주문의 이력을 조회할 권한이 없습니다.");
      }
      return;
    }

    // CUSTOMER는 자신의 주문만 조회 가능
    if (role == UserRole.CUSTOMER) {
      String orderUserId = order.getUser().getUserId();

      if (!orderUserId.equals(user.getUserId())) {
        throw new NotFoundException("해당 주문의 이력을 조회할 권한이 없습니다.");
      }
      return;
    }

    // 그 외의 경우 권한 없음
    throw new NotFoundException("해당 주문의 이력을 조회할 권한이 없습니다.");
  }
}