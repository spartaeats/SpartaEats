package com.sparta.sparta_eats.payment.application.service;

import com.sparta.sparta_eats.global.domain.exception.BadRequestException;
import com.sparta.sparta_eats.payment.domain.entity.Payment;
import com.sparta.sparta_eats.payment.domain.entity.PaymentIdempotency;
import com.sparta.sparta_eats.payment.domain.entity.PaymentLog;
import com.sparta.sparta_eats.payment.domain.model.PaymentEventType;
import com.sparta.sparta_eats.payment.domain.model.PaymentMethod;
import com.sparta.sparta_eats.payment.domain.model.PaymentStatus;
import com.sparta.sparta_eats.payment.domain.repository.PaymentIdempotencyRepository;
import com.sparta.sparta_eats.payment.domain.repository.PaymentLogRepository;
import com.sparta.sparta_eats.payment.domain.repository.PaymentRepository;
import com.sparta.sparta_eats.payment.exception.IdempotencyConflictException;
import com.sparta.sparta_eats.payment.infrastructure.client.TossPaymentsClient;
import com.sparta.sparta_eats.payment.infrastructure.config.TossProperties;
import com.sparta.sparta_eats.payment.presentation.dto.response.PaymentResponse;
import com.sparta.sparta_eats.order.domain.entity.Order;
import com.sparta.sparta_eats.order.domain.entity.OrderStatusHistory;
import com.sparta.sparta_eats.order.domain.repository.OrderRepository;
import com.sparta.sparta_eats.order.domain.repository.OrderStatusHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentLogRepository paymentLogRepository;
    private final PaymentIdempotencyRepository idempotencyRepository;

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    private final TossPaymentsClient tossClient;  // mock 모드에서도 주입 유지
    private final TossProperties tossProps;       // 추후 실연동 분기용

    private static final int IDEMPOTENCY_TTL_MINUTES = 10;

    @Override
    public UUID create(String idempotencyKey, UUID orderId, String userId, BigDecimal amount) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) throw new BadRequestException("Idempotency-Key 누락");
        if (orderId == null) throw new BadRequestException("orderId 누락");
        if (userId == null || userId.isBlank()) throw new BadRequestException("userId 누락");
        if (amount == null || amount.signum() <= 0) throw new BadRequestException("결제 금액이 유효하지 않습니다.");

        // (1) 멱등성: 동일 키면 기존 결제 반환
        var existingPayment = paymentRepository.findByIdempotencyKey(idempotencyKey);
        if (existingPayment.isPresent()) {
            saveLog(existingPayment.get().getId(), PaymentEventType.REQUEST, 200,
                    "{\"idempotency\":\"hit\"}", "{\"result\":\"existing\"}");
            return existingPayment.get().getId();
        }

        // (2) 멱등성 키 선점 (만료 전이면 충돌)
        var existingKey = idempotencyRepository.findById(idempotencyKey);
        if (existingKey.isPresent() && !isExpired(existingKey.get().getExpiredAt())) {
            throw new IdempotencyConflictException("동일 Idempotency-Key 처리 중입니다.");
        }

        // (3) 주문 조회 + 보안/금액 검증
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));

        // 주문자 일치 (Order.user.userId vs 요청 userId)
        String orderUserId = (order.getUser() != null) ? order.getUser().getUserId() : null;
        if (orderUserId == null || !userId.equals(orderUserId)) {
            throw new BadRequestException("주문자와 결제 요청자의 정보가 일치하지 않습니다.");
        }

        // 금액 일치 (Order.totalAmount(BigInteger) → BigDecimal 변환 메서드 사용 가정)
        if (order.getTotalAmountDecimal() == null || amount.compareTo(order.getTotalAmountDecimal()) != 0) {
            throw new BadRequestException("요청 금액이 주문 총액과 일치하지 않습니다.");
        }

        // (4) 결제 생성
        Payment payment = Payment.builder()
                .orderId(order.getId())
                .userId(userId) // String
                .amount(amount)
                .method(PaymentMethod.CARD)
                .status(PaymentStatus.PENDING)
                .build();
        payment.markRequested(idempotencyKey);
        paymentRepository.save(payment);

        // (5) 멱등성 키 저장 (TTL 10분)
        var idem = new PaymentIdempotency();
        idem.setIdempotencyKey(idempotencyKey);
        idem.setPaymentId(payment.getId());
        idem.setExpiredAt(LocalDateTime.now().plusMinutes(IDEMPOTENCY_TTL_MINUTES));
        idempotencyRepository.save(idem);

        // (6) 로그
        saveLog(payment.getId(), PaymentEventType.REQUEST, 200,
                jsonOf(Map.of("orderId", orderId, "userId", userId, "amount", amount.toString(), "idempotencyKey", idempotencyKey)),
                "{\"result\":\"created\"}");

        return payment.getId();
    }

    @Override
    public void confirm(UUID paymentId, String paymentKey) {
        if (paymentId == null) throw new BadRequestException("paymentId 누락");
        if (paymentKey == null || paymentKey.isBlank()) throw new BadRequestException("paymentKey 누락");

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("결제를 찾을 수 없습니다: " + paymentId));

        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("결제에 연결된 주문을 찾을 수 없습니다."));

        // --- Mock 승인 응답 ---
        Map<String, Object> mockResp = Map.of(
                "paymentKey", paymentKey,
                "orderId", order.getId().toString(),
                "approvedAt", LocalDateTime.now().toString(),
                "amount", payment.getAmount().toString()
        );

        // 결제 상태 전이
        payment.confirm(paymentKey); // PENDING -> CONFIRMED (+ confirmedAt)

        // 주문 상태/결제상태 반영 + 이력
        order.markPaymentPaid(); // paymentStatus = PAID, status = CONFIRMED(팀 룰)
        Long actorId = parseLongOrNull((order.getUser() != null) ? order.getUser().getUserId() : null);
        OrderStatusHistory hist = order.toStatusHistory(
                actorId,
                "CUSTOMER",
                Order.OrderStatus.CONFIRMED,   // Order의 enum 전달 (Order 내부에서 History enum으로 변환)
                null
        );
        orderStatusHistoryRepository.save(hist);

        // 로그
        saveLog(payment.getId(), PaymentEventType.CONFIRM, 200,
                jsonOf(Map.of("paymentKey", paymentKey, "idempotencyKey", payment.getIdempotencyKey())),
                jsonOf(mockResp));
    }

    @Override
    public void cancel(UUID paymentId, String reason) {
        if (paymentId == null) throw new BadRequestException("paymentId 누락");
        if (reason == null || reason.isBlank()) throw new BadRequestException("취소 사유 누락");

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("결제를 찾을 수 없습니다: " + paymentId));

        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("결제에 연결된 주문을 찾을 수 없습니다."));

        // 도메인 규칙대로 취소 (PENDING에서만 허용)
        payment.cancel(reason);

        // 주문 취소 반영 + 이력
        order.markPaymentCanceled(reason);
        Long actorId = parseLongOrNull((order.getUser() != null) ? order.getUser().getUserId() : null);
        OrderStatusHistory hist = order.toStatusHistory(
                actorId,
                "CUSTOMER",
                Order.OrderStatus.CANCELED,
                reason
        );
        orderStatusHistoryRepository.save(hist);

        // 로그
        saveLog(payment.getId(), PaymentEventType.CANCEL, 200,
                jsonOf(Map.of("reason", reason, "idempotencyKey", payment.getIdempotencyKey())),
                "{\"result\":\"canceled\"}");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> search(Pageable pageable, Boolean includeDeleted) {
        var page = Boolean.TRUE.equals(includeDeleted)
                ? paymentRepository.findAllIncludingDeleted(pageable)
                : paymentRepository.findAll(pageable);
        return page.map(this::toResponse);
    }

    /* ===== 내부 유틸 ===== */

    private boolean isExpired(LocalDateTime expiredAt) {
        return expiredAt == null || LocalDateTime.now().isAfter(expiredAt);
    }

    private void saveLog(UUID paymentId, PaymentEventType type, Integer httpStatus, String req, String resp) {
        PaymentLog log = new PaymentLog();
        log.setPaymentId(paymentId);
        log.setEventType(type);
        log.setHttpStatus(httpStatus);
        log.setRequestBody(req);
        log.setResponseBody(resp);
        paymentLogRepository.save(log);
    }

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getOrderId(),
                p.getUserId(),
                p.getAmount(),
                p.getMethod(),
                p.getStatus(),
                p.getCreatedAt(),
                p.getConfirmedAt(),
                p.getCanceledAt()
        );
    }

    private String jsonOf(Map<String, ?> m) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, ?> e : m.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(e.getKey()).append("\":\"").append(String.valueOf(e.getValue())).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    private Long parseLongOrNull(String s) {
        try {
            return (s == null || s.isBlank()) ? null : Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null; // 알파넘릭 userId 대응
        }
    }
}
