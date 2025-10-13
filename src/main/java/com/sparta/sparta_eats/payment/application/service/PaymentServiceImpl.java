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
import com.sparta.sparta_eats.payment.infrastructure.client.TossPaymentsClient;     // ✅
import com.sparta.sparta_eats.payment.infrastructure.config.TossProperties;       // ✅
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

    private final TossPaymentsClient tossClient;
    private final TossProperties tossProps;

    @Override
    public UUID create(String idemKey, UUID orderId, UUID userId, long amount) {
        var existed = payments.findByIdempotencyKey(idemKey);
        if (existed.isPresent()) return existed.get().getId();

        try {
            var p = new Payment();
            p.setId(UUID.randomUUID());
            p.setOrderId(orderId);
            p.setUserId(userId);
            p.setAmount(amount);
            p.setMethod(PaymentMethod.CARD);
            p.setStatus(PaymentStatus.PENDING);
            p.setIdempotencyKey(idemKey);
            p.setStartedAt(LocalDateTime.now());
            payments.save(p);

            var idem = new PaymentIdempotency();
            idem.setIdempotencyKey(idemKey);
            idem.setPaymentId(p.getId());
            idem.setExpiredAt(LocalDateTime.now().plusHours(48));
            idems.save(idem);

            saveLog(p.getId(), PaymentEventType.REQUEST, 200,
                    Map.of("action","create","orderId",orderId,"userId",userId,"amount",amount).toString(),
                    "{ok}");

            return p.getId();
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            var found = payments.findByIdempotencyKey(idemKey);
            if (found.isPresent()) return found.get().getId();
            throw new BadRequestException("IDEMPOTENCY_CONFLICT");
        }
    }

    @Override
    public void confirm(UUID paymentId, String paymentKey) {
        var p = payments.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("결제를 찾을 수 없습니다."));

        if (p.getStatus() != PaymentStatus.PENDING) {
            throw new BadRequestException("PENDING 상태에서만 확정할 수 있습니다.");
        }

        if (tossProps.mockEnabled()) { // ✅ mock 스위치
            p.setStatus(PaymentStatus.CONFIRMED);
            p.setConfirmedAt(LocalDateTime.now());
            p.setPaymentKey(paymentKey);
            saveLog(p.getId(), PaymentEventType.CONFIRM, 200, "{mock-confirm}", "{ok}");
            return;
        }

        String idemKey = (p.getIdempotencyKey() != null) ? p.getIdempotencyKey() : "confirm-" + p.getId();

        Map<String, Object> res = tossClient.confirm(
                paymentKey,
                p.getOrderId().toString(),
                p.getAmount(),
                idemKey
        );

        Object status = res.get("status");
        Object approvedAt = res.get("approvedAt");
        if ("DONE".equals(status) || "SUCCESS".equals(status)) {
            p.setStatus(PaymentStatus.CONFIRMED);
            // approvedAt 이 ISO 문자열인 경우가 많음 → 필요시 파싱 유연화
            if (approvedAt != null) {
                p.setConfirmedAt(LocalDateTime.parse(String.valueOf(approvedAt)));
            } else {
                p.setConfirmedAt(LocalDateTime.now());
            }
            p.setPaymentKey(paymentKey);
        } else {
            throw new BadRequestException("토스 승인 실패: status=" + status);
        }

        saveLog(p.getId(), PaymentEventType.CONFIRM, 200, "{confirm}", res.toString());
    }

    @Override
    public void cancel(UUID paymentId, String reason) {
        var p = payments.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("결제를 찾을 수 없습니다."));

        if (p.getStatus() == PaymentStatus.CANCELED) return;
        if (p.getStatus() == PaymentStatus.FAILED) {
            throw new BadRequestException("FAILED 상태는 취소할 수 없습니다.");
        }

        if (tossProps.mockEnabled()) {
            p.setStatus(PaymentStatus.CANCELED);
            p.setCanceledAt(LocalDateTime.now());
            p.setCancelReason(reason);
            saveLog(p.getId(), PaymentEventType.CANCEL, 200, "{mock-cancel}", "{ok}");
            return;
        }

        if (p.getPaymentKey() == null) {
            throw new BadRequestException("paymentKey가 없습니다. (승인 기록 필요)");
        }

        String idemKey = "cancel-" + p.getId();
        Map<String, Object> res = tossClient.cancel(p.getPaymentKey(), reason, null, idemKey);

        Object status = res.get("status");
        Object canceledAt = res.get("canceledAt");
        if ("CANCELED".equals(status)) {
            p.setStatus(PaymentStatus.CANCELED);
            if (canceledAt != null) {
                p.setCanceledAt(LocalDateTime.parse(String.valueOf(canceledAt)));
            } else {
                p.setCanceledAt(LocalDateTime.now());
            }
            p.setCancelReason(reason);
        } else {
            throw new BadRequestException("토스 취소 실패: status=" + status);
        }

        saveLog(p.getId(), PaymentEventType.CANCEL, 200, "{cancel}", res.toString());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<?> search(Pageable pageable, Boolean includeDeleted) {
        if (Boolean.TRUE.equals(includeDeleted)) return payments.findAll(pageable);
        Specification<Payment> spec = PaymentSpecs.paymentNotDeleted();
        return payments.findAll(spec, pageable);
    }

    private void saveLog(UUID paymentId, PaymentEventType type, Integer httpStatus, String reqBody, String resBody) {
        var log = new PaymentLog();
        log.setPaymentId(paymentId);
        log.setEventType(type);
        log.setHttpStatus(httpStatus);
        log.setRequestBody(reqBody);
        log.setResponseBody(resBody);
        logs.save(log);
    }
}
