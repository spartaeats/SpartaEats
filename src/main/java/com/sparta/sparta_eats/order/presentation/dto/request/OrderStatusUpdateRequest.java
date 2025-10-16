package com.sparta.sparta_eats.order.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문 상태 변경 요청 DTO
 */
@Getter
@NoArgsConstructor
public class OrderStatusUpdateRequest {

  @NotBlank(message = "변경할 상태를 입력해주세요.")
  private String status;

  private String cancelReason;
}