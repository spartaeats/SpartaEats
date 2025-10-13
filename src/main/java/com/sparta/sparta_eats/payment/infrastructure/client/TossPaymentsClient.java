package com.sparta.sparta_eats.payment.infrastructure.client;

import com.sparta.sparta_eats.payment.infrastructure.config.TossProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
public class TossPaymentsClient {

    private final RestClient rest;
    private final TossProperties props;

    public TossPaymentsClient(TossProperties props) {
        this.props = props;
        this.rest = RestClient.builder()
                .baseUrl(props.baseUrl())
                .build();
    }

    private String basicAuth() {
        String token = Base64.getEncoder()
                .encodeToString((props.secretKey() + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + token;
    }

    /** 결제 승인 */
    public Map<String, Object> confirm(String paymentKey, String orderId, long amount, String idempotencyKey) {
        return rest.post()
                .uri("/v1/payments/confirm")
                .header(HttpHeaders.AUTHORIZATION, basicAuth())
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("paymentKey", paymentKey, "orderId", orderId, "amount", amount))
                .retrieve()
                .body(Map.class);
    }

    /** 결제 취소 */
    public Map<String, Object> cancel(String paymentKey, String reason, Long cancelAmount, String idempotencyKey) {
        Map<String, Object> body = (cancelAmount == null)
                ? Map.of("cancelReason", reason)
                : Map.of("cancelReason", reason, "cancelAmount", cancelAmount);

        return rest.post()
                .uri("/v1/payments/{paymentKey}/cancel", paymentKey)
                .header(HttpHeaders.AUTHORIZATION, basicAuth())
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);
    }
}
