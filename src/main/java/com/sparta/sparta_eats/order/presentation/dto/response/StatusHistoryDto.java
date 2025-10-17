package com.sparta.sparta_eats.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.sparta_eats.order.domain.entity.OrderStatusHistory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 개별 상태 이력 DTO
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusHistoryDto {

  private UUID id;
  private String actorRole;
  private String status;
  private String cancelReason;
  private LocalDateTime createdAt;

  /**
   * Entity -> DTO 변환
   */
  public static StatusHistoryDto from(OrderStatusHistory history) {
    return StatusHistoryDto.builder()
        .id(history.getId())
        .actorRole(history.getActorRole())
        .status(history.getStatus().name())
        .cancelReason(history.getCancelReason())
        .createdAt(history.getCreatedAt())
        .build();
  }
}