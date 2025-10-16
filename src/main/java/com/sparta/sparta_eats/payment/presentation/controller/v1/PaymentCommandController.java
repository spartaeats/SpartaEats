package com.sparta.sparta_eats.payment.presentation.controller.v1;

import com.sparta.sparta_eats.payment.application.service.PaymentService;
import com.sparta.sparta_eats.payment.presentation.dto.request.PaymentCancelRequest;
import com.sparta.sparta_eats.payment.presentation.dto.request.PaymentConfirmRequest;
import com.sparta.sparta_eats.payment.presentation.dto.request.PaymentCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments")
public class PaymentCommandController {

    private final PaymentService service;

    @PostMapping
    @Operation(summary = "결제 생성 (멱등성 지원: 헤더 Idempotency-Key 우선)")
    public ResponseEntity<Map<String, Object>> create(
            @RequestHeader(value="Idempotency-Key", required=false) String idemHeader,
            @Valid @RequestBody PaymentCreateRequest req
    ) {
        String key = (idemHeader != null && !idemHeader.isBlank()) ? idemHeader : req.idempotencyKey();
        var id = service.create(key, req.orderId(), req.userId(), req.amount());
        return ResponseEntity.ok(Map.of("paymentId", id));
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "결제 확정")
    public ResponseEntity<Void> confirm(
            @PathVariable("id") UUID paymentId,
            @Valid @RequestBody PaymentConfirmRequest req
    ) {
        service.confirm(paymentId, req.paymentKey());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "결제 취소")
    public ResponseEntity<Void> cancel(
            @PathVariable("id") UUID paymentId,
            @Valid @RequestBody PaymentCancelRequest req
    ) {
        service.cancel(paymentId, req.reason());
        return ResponseEntity.noContent().build();
    }
}
