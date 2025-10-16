//package com.sparta.sparta_eats.payment.application.service;
//
//import com.sparta.sparta_eats.order.domain.entity.Order;
//import com.sparta.sparta_eats.payment.application.service.PaymentService;
//import com.sparta.sparta_eats.payment.domain.entity.Payment;
//import com.sparta.sparta_eats.payment.domain.model.PaymentMethod;
//import com.sparta.sparta_eats.payment.domain.model.PaymentStatus;
//import com.sparta.sparta_eats.payment.domain.repository.PaymentRepository;
//import com.sparta.sparta_eats.payment.presentation.dto.response.PaymentResponse;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.*;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class PaymentServiceImpl implements PaymentService {
//
//    private final PaymentRepository paymentRepository;
////    private final OrderRepository orderRepository;
//
//    @Override
//    public UUID create(String idempotencyKey, UUID orderId, UUID userId, BigDecimal amount) {
//        // 1) 멱등성: 기존 요청 재시도면 기존 결제 반환
//        var existing = paymentRepository.findByIdempotencyKey(idempotencyKey);
//        if (existing.isPresent()) {
//            return existing.get().getId();
//        }
//
//        // 2) 주문 검증 + 금액 일치 검증
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));
//
//        if (amount == null || amount.signum() <= 0) {
//            throw new IllegalArgumentException("결제 금액이 유효하지 않습니다.");
//        }
//        if (order.getTotalAmount() == null) {
//            throw new IllegalStateException("주문 총액이 계산되지 않았습니다.");
//        }
//        if (amount.compareTo(order.getTotalAmount()) != 0) {
//            throw new IllegalArgumentException("요청 금액이 주문 총액과 일치하지 않습니다.");
//        }
//
//        // 3) 결제 생성 (카드만 지원)
//        Payment payment = Payment.builder()
//                .orderId(order.getId())
//                .amount(amount)
//                .method(PaymentMethod.CARD)
//                .status(PaymentStatus.PENDING) // markRequested에서 검증 겸 재설정
//                .build();
//
//        payment.markRequested(idempotencyKey);
//        paymentRepository.save(payment);
//        return payment.getId();
//    }
//
//    @Override
//    public void confirm(UUID paymentId, String paymentKey) {
//        Payment payment = paymentRepository.findById(paymentId)
//                .orElseThrow(() -> new EntityNotFoundException("결제를 찾을 수 없습니다: " + paymentId));
//        payment.confirm(paymentKey);
//        // Order와의 동기화가 필요하면 여기서 주문 paymentStatus도 업데이트 (아직 OrderService 없으니 패스)
//    }
//
//    @Override
//    public void cancel(UUID paymentId, String reason) {
//        Payment payment = paymentRepository.findById(paymentId)
//                .orElseThrow(() -> new EntityNotFoundException("결제를 찾을 수 없습니다: " + paymentId));
//        payment.cancel(reason);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public Page<?> search(Pageable pageable, Boolean includeDeleted) {
//        Page<Payment> page = (Boolean.TRUE.equals(includeDeleted))
//                ? paymentRepository.findAllIncludingDeleted(pageable)
//                : paymentRepository.findAll(pageable);
//
//        return page.map(this::toResponse);
//    }
//
//    private PaymentResponse toResponse(Payment p) {
//        return new PaymentResponse(
//                p.getId(),
//                p.getOrderId(),
//                /* userId: 지금 Payment 엔티티에 userId 필드가 없으므로 null or 확장 */
//                null,
//                p.getAmount(),
//                p.getMethod(),
//                p.getStatus(),
//                p.getCreatedAt(),
//                // confirmedAt/canceledAt 추적 칼럼이 없다면 null 그대로
//                null,
//                null
//        );
//    }
//}
