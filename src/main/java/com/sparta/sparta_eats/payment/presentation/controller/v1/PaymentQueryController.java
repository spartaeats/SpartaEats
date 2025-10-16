package com.sparta.sparta_eats.payment.presentation.controller.v1;

import com.sparta.sparta_eats.payment.application.service.PaymentService;
import com.sparta.sparta_eats.payment.presentation.dto.response.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments")
public class PaymentQueryController {

    private final PaymentService service;

    @GetMapping
    public ResponseEntity<Page<PaymentResponse>> list(Pageable pageable,
                                                      @RequestParam(required = false) Boolean includeDeleted) {
        // page size 강제
        int size = pageable.getPageSize();
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        // 기본 정렬: createdAt DESC
        Sort sort = pageable.getSort().isUnsorted()
                ? Sort.by(Sort.Direction.DESC, "createdAt")
                : pageable.getSort();

        pageable = PageRequest.of(pageable.getPageNumber(), size, sort);
        return ResponseEntity.ok(service.search(pageable, includeDeleted));
    }

}
