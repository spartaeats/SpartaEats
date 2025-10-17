package com.sparta.sparta_eats.order.application.service;

import com.sparta.sparta_eats.global.domain.exception.BadRequestException;
import com.sparta.sparta_eats.global.domain.exception.NotFoundException;
import com.sparta.sparta_eats.order.domain.entity.Order;
import com.sparta.sparta_eats.order.domain.entity.OrderStatusHistory;
import com.sparta.sparta_eats.order.domain.repository.OrderRepository;
import com.sparta.sparta_eats.order.domain.repository.OrderStatusHistoryRepository;
import com.sparta.sparta_eats.order.presentation.dto.request.OrderStatusUpdateRequest;
import com.sparta.sparta_eats.order.presentation.dto.response.OrderStatusUpdateResponse;
import com.sparta.sparta_eats.store.domain.entity.Store;
import com.sparta.sparta_eats.user.domain.entity.User;
import com.sparta.sparta_eats.user.domain.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 주문 상태 변경 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OrderStatusService {

  private final OrderRepository orderRepository;
  private final OrderStatusHistoryRepository orderStatusHistoryRepository;

  private static final long CUSTOMER_CANCEL_WINDOW_MINUTES = 5;

  /**
   * 주문 상태 변경
   *
   * @param orderId 주문 ID
   * @param request 상태 변경 요청
   * @param user 요청한 사용자
   * @return 상태 변경 응답
   */
  public OrderStatusUpdateResponse updateOrderStatus(
      UUID orderId,
      OrderStatusUpdateRequest request,
      User user
  ) {
    // 1. 주문 존재 확인
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다."));

    // 2. 상태 값 검증 및 변환
    Order.OrderStatus newStatus = parseAndValidateStatus(request.getStatus());

    // 3. 취소 상태일 경우 사유 필수 체크
    if (newStatus == Order.OrderStatus.CANCELED &&
        (request.getCancelReason() == null || request.getCancelReason().isBlank())) {
      throw new BadRequestException("취소 상태로 변경 시 취소 사유는 필수입니다.");
    }

    // 4. 권한 및 비즈니스 규칙 검증
    validateStatusChangePermission(order, newStatus, user);

    // 5. 이전 상태 저장
    Order.OrderStatus previousStatus = order.getStatus();

    // 6. 주문 상태 변경
    changeOrderStatus(order, newStatus, request.getCancelReason());

    // 7. 상태 이력 저장
    saveStatusHistory(order, user, newStatus, request.getCancelReason());

    // 8. 주문 저장
    orderRepository.save(order);

    // 9. 응답 생성
    return OrderStatusUpdateResponse.of(
        order.getId(),
        previousStatus.name(),
        newStatus.name(),
        order.getUpdatedAt(),
        order.getCanceledAt(),
        order.getCancelReason()
    );
  }

  /**
   * 상태 문자열을 Enum으로 변환 및 검증
   */
  private Order.OrderStatus parseAndValidateStatus(String statusStr) {
    try {
      return Order.OrderStatus.valueOf(statusStr.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("유효하지 않은 상태 값입니다: " + statusStr);
    }
  }

  /**
   * 상태 변경 권한 및 비즈니스 규칙 검증
   */
  private void validateStatusChangePermission(
      Order order,
      Order.OrderStatus newStatus,
      User user
  ) {
    UserRole role = user.getRole();

    // CUSTOMER 권한 검증
    if (role == UserRole.CUSTOMER) {
      validateCustomerStatusChange(order, newStatus, user);
      return;
    }

    // OWNER 권한 검증
    if (role == UserRole.OWNER) {
      validateOwnerStatusChange(order, user);
      return;
    }

    // MANAGER, MASTER는 모든 상태 변경 가능
    if (role == UserRole.MANAGER || role == UserRole.MASTER) {
      return;
    }

    throw new BadRequestException("주문 상태를 변경할 권한이 없습니다.");
  }

  /**
   * 고객 상태 변경 검증
   * - 본인 주문만 가능
   * - PLACED 상태에서만 취소 가능
   * - 주문 후 5분 이내만 취소 가능
   */
  private void validateCustomerStatusChange(
      Order order,
      Order.OrderStatus newStatus,
      User user
  ) {
    // 본인 주문 확인
    if (!order.getUser().getUserId().equals(user.getUserId())) {
      throw new BadRequestException("본인의 주문만 변경할 수 있습니다.");
    }

    // 고객은 취소만 가능
    if (newStatus != Order.OrderStatus.CANCELED) {
      throw new BadRequestException("고객은 주문 취소만 가능합니다.");
    }

    // PLACED 상태에서만 취소 가능
    if (order.getStatus() != Order.OrderStatus.PLACED) {
      throw new BadRequestException("주문 접수 상태에서만 취소가 가능합니다.");
    }

    // 5분 이내 취소 가능
    LocalDateTime orderTime = order.getCreatedAt();
    LocalDateTime now = LocalDateTime.now();
    long minutesPassed = Duration.between(orderTime, now).toMinutes();

    if (minutesPassed > CUSTOMER_CANCEL_WINDOW_MINUTES) {
      throw new BadRequestException(
          String.format("주문 후 %d분 이내에만 취소가 가능합니다.", CUSTOMER_CANCEL_WINDOW_MINUTES)
      );
    }
  }

  /**
   * 점주 상태 변경 검증
   * - 본인 매장 주문만 가능
   */
  private void validateOwnerStatusChange(Order order, User user) {
    Store store = order.getStore();
    String storeOwnerId = store.getOwner().getUserId();

    if (!storeOwnerId.equals(user.getUserId())) {
      throw new BadRequestException("본인 매장의 주문만 변경할 수 있습니다.");
    }
  }

  /**
   * 주문 상태 변경 처리
   */
  private void changeOrderStatus(
      Order order,
      Order.OrderStatus newStatus,
      String cancelReason
  ) {
    if (newStatus == Order.OrderStatus.CANCELED) {
      order.cancel(cancelReason);
    } else {
      order.updateStatus(newStatus);
    }
  }

  /**
   * 상태 이력 저장
   */
  private void saveStatusHistory(
      Order order,
      User user,
      Order.OrderStatus newStatus,
      String cancelReason
  ) {
    OrderStatusHistory history = OrderStatusHistory.builder()
        .order(order)
        .actorId(user.getUserId()) // ⭐ String으로 바로 사용
        .actorRole(user.getRole().name())
        .status(OrderStatusHistory.OrderStatus.valueOf(newStatus.name()))
        .cancelReason(cancelReason)
        .build();

    orderStatusHistoryRepository.save(history);
  }
}