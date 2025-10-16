package com.sparta.sparta_eats.order.presentation;

import com.sparta.sparta_eats.global.infrastructure.config.security.UserDetailsImpl;
import com.sparta.sparta_eats.order.application.service.OrderService;
import com.sparta.sparta_eats.order.presentation.dto.request.OrderCreateRequest;
import com.sparta.sparta_eats.order.presentation.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @RequestBody OrderCreateRequest request) {

        return ResponseEntity.created(URI.create("temp"))
                .body(orderService.createOrder(userDetails.getUser(), request));
    }
}
