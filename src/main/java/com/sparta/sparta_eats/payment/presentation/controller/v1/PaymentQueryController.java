package com.sparta.sparta_eats.payment.presentation.controller.v1;

import com.sparta.sparta_eats.payment.application.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments")
public class PaymentQueryController {

    private final PaymentService service;

    @GetMapping
    @Operation(summary = "결제 목록 조회 (삭제 제외가 기본)")
    public ResponseEntity<Page<?>> list(Pageable pageable,
                                        @RequestParam(required = false) Boolean includeDeleted) {
        return ResponseEntity.ok(service.search(pageable, includeDeleted));
    }
}
