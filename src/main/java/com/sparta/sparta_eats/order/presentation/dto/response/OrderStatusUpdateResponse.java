package com.sparta.sparta_eats.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 주문 상태 변경 응답 DTO
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderStatusUpdateResponse {

  private UUID id;
  private String previousStatus;
  private String status;
  private LocalDateTime updatedAt;
  private LocalDateTime canceledAt;
  private String cancelReason;

  /**
   * 응답 생성
   */
  public static OrderStatusUpdateResponse of(
      UUID orderId,
      String previousStatus,
      String currentStatus,
      LocalDateTime updatedAt,
      LocalDateTime canceledAt,
      String cancelReason
  ) {
    return OrderStatusUpdateResponse.builder()
        .id(orderId)
        .previousStatus(previousStatus)
        .status(currentStatus)
        .updatedAt(updatedAt)
        .canceledAt(canceledAt)
        .cancelReason(cancelReason)
        .build();
  }
}