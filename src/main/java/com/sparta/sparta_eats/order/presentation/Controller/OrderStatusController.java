package com.sparta.sparta_eats.order.presentation.controller;

import com.sparta.sparta_eats.global.infrastructure.config.security.UserDetailsImpl;
import com.sparta.sparta_eats.order.application.service.OrderStatusService;
import com.sparta.sparta_eats.order.presentation.dto.request.OrderStatusUpdateRequest;
import com.sparta.sparta_eats.order.presentation.dto.response.OrderStatusUpdateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 주문 상태 변경 컨트롤러
 */
@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
@Tag(name = "주문 상태 변경", description = "주문 상태 변경 API")
public class OrderStatusController {

  private final OrderStatusService orderStatusService;

  /**
   * 주문 상태 변경
   *
   * @param orderId 주문 ID
   * @param request 상태 변경 요청
   * @param userDetails 인증된 사용자 정보
   * @return 상태 변경 결과
   */
  @PatchMapping("/{orderId}/status")
  @Operation(summary = "주문 상태 변경", description = "주문의 상태를 변경하고 이력을 기록합니다.")
  public ResponseEntity<OrderStatusUpdateResponse> updateOrderStatus(
      @PathVariable UUID orderId,
      @Valid @RequestBody OrderStatusUpdateRequest request,
      @AuthenticationPrincipal UserDetailsImpl userDetails
  ) {
    OrderStatusUpdateResponse response = orderStatusService
        .updateOrderStatus(orderId, request, userDetails.getUser());

    return ResponseEntity.ok(response);
  }
}