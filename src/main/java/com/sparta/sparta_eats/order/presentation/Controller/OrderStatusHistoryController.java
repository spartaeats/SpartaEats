package com.sparta.sparta_eats.order.presentation.controller;

import com.sparta.sparta_eats.global.infrastructure.config.security.UserDetailsImpl;
import com.sparta.sparta_eats.order.application.service.OrderStatusHistoryService;
import com.sparta.sparta_eats.order.presentation.dto.response.OrderStatusHistoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 주문 상태 이력 컨트롤러
 */
@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
@Tag(name = "주문 상태 이력", description = "주문 상태 이력 조회 API")
public class OrderStatusHistoryController {

  private final OrderStatusHistoryService orderStatusHistoryService;

  /**
   * 주문 상태 이력 조회
   *
   * @param orderId 주문 ID
   * @param userDetails 인증된 사용자 정보
   * @return 주문 상태 이력
   */
  @GetMapping("/{orderId}/history")
  @Operation(summary = "주문 상태 이력 조회", description = "특정 주문의 상태 변경 이력을 시간순으로 조회합니다.")
  public ResponseEntity<OrderStatusHistoryResponse> getOrderStatusHistory(
      @PathVariable UUID orderId,
      @AuthenticationPrincipal UserDetailsImpl userDetails
  ) {
    OrderStatusHistoryResponse response = orderStatusHistoryService
        .getOrderStatusHistory(orderId, userDetails.getUser());

    return ResponseEntity.ok(response);
  }
}