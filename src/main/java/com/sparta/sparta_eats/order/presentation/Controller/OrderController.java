package com.sparta.sparta_eats.order.presentation.Controller;

import com.sparta.sparta_eats.global.infrastructure.config.security.UserDetailsImpl;
import com.sparta.sparta_eats.order.application.service.OrderService;
import com.sparta.sparta_eats.order.presentation.dto.request.OrderCreateRequest;
import com.sparta.sparta_eats.order.presentation.dto.request.OrderSearchCondition;
import com.sparta.sparta_eats.order.presentation.dto.response.OrderCreateResponse;
import com.sparta.sparta_eats.order.presentation.dto.response.OrderListResponse;
import com.sparta.sparta_eats.order.presentation.dto.response.OrderSingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderCreateResponse> createOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                           @RequestBody OrderCreateRequest request) {

        return ResponseEntity.created(URI.create("temp"))
                .body(orderService.createOrder(userDetails.getUser(), request));
    }

    @GetMapping
    public ResponseEntity<Page<OrderListResponse>> getOrderList(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                Pageable pageable,
                                                                OrderSearchCondition condition
                                                                ) {
        return ResponseEntity.ok(orderService.searchOrders(userDetails.getUser(), condition, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderSingleResponse> getOrderDetail(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderDetail(userDetails.getUser(), id));
    }
}
