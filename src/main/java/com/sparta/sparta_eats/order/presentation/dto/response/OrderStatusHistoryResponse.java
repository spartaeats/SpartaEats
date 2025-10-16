package com.sparta.sparta_eats.order.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * 주문 상태 이력 조회 응답 DTO
 */
@Getter
@Builder
public class OrderStatusHistoryResponse {

  private UUID orderId;
  private List<StatusHistoryDto> history;

  /**
   * 응답 생성
   */
  public static OrderStatusHistoryResponse of(UUID orderId, List<StatusHistoryDto> history) {
    return OrderStatusHistoryResponse.builder()
        .orderId(orderId)
        .history(history)
        .build();
  }
}