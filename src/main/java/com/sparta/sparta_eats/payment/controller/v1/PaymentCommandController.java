package com.sparta.sparta_eats.payment.controller.v1;

import com.sparta.sparta_eats.payment.application.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments")
public class PaymentCommandController {

    private final PaymentService service;

    @PostMapping
    @Operation(summary = "결제 생성 (멱등성 지원)")
    public ResponseEntity<Map<String, Object>> create(
            @RequestParam String idempotencyKey,
            @RequestParam UUID orderId,
            @RequestParam UUID userId,
            @RequestParam long amount
    ) {
        var id = service.create(idempotencyKey, orderId, userId, amount);
        return ResponseEntity.ok(Map.of("paymentId", id));
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "결제 확정")
    public ResponseEntity<Void> confirm(
            @PathVariable("id") UUID paymentId,
            @RequestParam String paymentKey
    ) {
        service.confirm(paymentId, paymentKey);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "결제 취소")
    public ResponseEntity<Void> cancel(
            @PathVariable("id") UUID paymentId,
            @RequestParam String reason
    ) {
        service.cancel(paymentId, reason);
        return ResponseEntity.noContent().build();
    }
}
