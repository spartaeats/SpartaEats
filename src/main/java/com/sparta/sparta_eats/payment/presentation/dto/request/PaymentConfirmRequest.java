package com.sparta.sparta_eats.payment.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PaymentConfirmRequest(
        @Schema(example = "PG-1234567890")
        @NotBlank String paymentKey
) { }
