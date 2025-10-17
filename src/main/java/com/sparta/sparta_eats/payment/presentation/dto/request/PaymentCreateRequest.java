package com.sparta.sparta_eats.payment.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record PaymentCreateRequest(
        @Schema(example = "idem-20251015-0001")
        @NotBlank String idempotencyKey,

        @Schema(example = "e2c4b98f-2a7a-42c2-9e21-8b4e9c1b1a10")
        @NotNull UUID orderId,

        @Schema(example = "5e9f1d10-1a7b-4f0e-8a6f-44b2f2ca73a9")
        @NotNull String userId,

        @Schema(example = "23900", description = "결제 금액(원). BigDecimal이지만 정수만 허용")
        @Positive BigDecimal amount
) { }
