package com.sparta.sparta_eats.payment.application.service;

import com.sparta.sparta_eats.global.domain.exception.BadRequestException;
import com.sparta.sparta_eats.global.domain.exception.NotFoundException;
import com.sparta.sparta_eats.payment.domain.entity.Payment;
import com.sparta.sparta_eats.payment.domain.entity.PaymentIdempotency;
import com.sparta.sparta_eats.payment.domain.entity.PaymentLog;
import com.sparta.sparta_eats.payment.domain.model.PaymentEventType;
import com.sparta.sparta_eats.payment.domain.model.PaymentMethod;
import com.sparta.sparta_eats.payment.domain.model.PaymentStatus;
import com.sparta.sparta_eats.payment.domain.repository.PaymentIdempotencyRepository;
import com.sparta.sparta_eats.payment.domain.repository.PaymentLogRepository;
import com.sparta.sparta_eats.payment.domain.repository.PaymentRepository;
import com.sparta.sparta_eats.payment.infrastructure.persistence.spec.PaymentSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository payments;
    private final PaymentIdempotencyRepository idems;
    private final PaymentLogRepository logs;

    /**
     *  결제 생성 (멱등성 지원)
     */
    @Override
    public UUID create(String idemKey, UUID orderId, UUID userId, long amount) {
        // (1) 멱등성
        var existed = payments.findByIdempotencyKey(idemKey);
        if (existed.isPresent()) {
            // 재요청 → 기존 결제 반환
            return existed.get().getId();
        }

        // (2) 결제 엔티티 생성(PENDING)
        var p = new Payment();
        p.setId(UUID.randomUUID()); // @GeneratedValue 없을 때를 대비
        p.setOrderId(orderId);
        p.setUserId(userId);
        p.setAmount(amount);
        p.setMethod(PaymentMethod.CARD);
        p.setStatus(PaymentStatus.PENDING);
        p.setIdempotencyKey(idemKey);
        p.setStartedAt(LocalDateTime.now());
        payments.save(p);

        // (3) 멱등성 키 보관 (예: 48시간)
        var idem = new PaymentIdempotency();
        idem.setIdempotencyKey(idemKey);
        idem.setPaymentId(p.getId());
        idem.setExpiredAt(LocalDateTime.now().plusHours(48));
        idems.save(idem);

        // (4) 로그
        saveLog(p.getId(), PaymentEventType.REQUEST, 200, Map.of(
                "action", "create",
                "orderId", orderId,
                "userId", userId,
                "amount", amount
        ).toString(), "{ok}");

        return p.getId();
    }

    /**
     *  결제 확정(승인)
     */
    @Override
    public void confirm(UUID paymentId, String paymentKey) {
        var p = payments.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("결제를 찾을 수 없습니다."));

        if (p.getStatus() != PaymentStatus.PENDING) {
            throw new BadRequestException("PENDING 상태에서만 확정할 수 있습니다.");
        }
        p.setStatus(PaymentStatus.CONFIRMED);
        p.setConfirmedAt(LocalDateTime.now());
        p.setPaymentKey(paymentKey);

        saveLog(p.getId(), PaymentEventType.CONFIRM, 200,
                Map.of("action", "confirm", "paymentKey", paymentKey).toString(), "{ok}");
    }

    /**
     *  결제 취소
     */
    @Override
    public void cancel(UUID paymentId, String reason) {
        var p = payments.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("결제를 찾을 수 없습니다."));

        if (p.getStatus() == PaymentStatus.CANCELED) {
            // 이미 취소 → idempotent
            return;
        }
        if (p.getStatus() == PaymentStatus.FAILED) {
            throw new BadRequestException("FAILED 상태는 취소할 수 없습니다.");
        }
        p.setStatus(PaymentStatus.CANCELED);
        p.setCanceledAt(LocalDateTime.now());
        p.setCancelReason(reason);

        saveLog(p.getId(), PaymentEventType.CANCEL, 200,
                Map.of("action", "cancel", "reason", reason).toString(), "{ok}");
    }

    /**
     * 간단 검색: 삭제 제외가 기본 (includeDeleted=true면 전체)
     */
    @Override
    @Transactional(readOnly = true)
    public Page<?> search(Pageable pageable, Boolean includeDeleted) {
        if (Boolean.TRUE.equals(includeDeleted)) {
            return payments.findAll(pageable);
        }
        Specification<Payment> spec = PaymentSpecs.paymentNotDeleted();
        return payments.findAll(spec, pageable);
    }

    private void saveLog(UUID paymentId, PaymentEventType type, Integer httpStatus,
                         String reqBody, String resBody) {
        var log = new PaymentLog();
        log.setPaymentId(paymentId);
        log.setEventType(type);
        log.setHttpStatus(httpStatus);
        log.setRequestBody(reqBody);
        log.setResponseBody(resBody);
        logs.save(log);
    }
}
