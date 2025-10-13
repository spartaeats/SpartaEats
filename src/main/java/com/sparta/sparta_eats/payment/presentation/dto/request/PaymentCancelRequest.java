package com.sparta.sparta_eats.payment.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PaymentCancelRequest(
        @Schema(example = "사용자 요청으로 취소")
        @NotBlank String reason
) { }
