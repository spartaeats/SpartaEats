package com.sparta.sparta_eats.ai.application.service;

import com.sparta.sparta_eats.ai.infrastructure.api.GeminiApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiServiceV1 {
    private final GeminiApiClient geminiApiClient;

    public ResponseEntity<String> summarizeReview(Object Product) {
        String summary = geminiApiClient.reviewSummary(List.of("순대국이 짜다", "순대가 당면 순대임 미친", "소주가 비쌈", "순대 꽉찬게 맛있음", "머릿고기 너무 쫄깃해서 좋음", "포장 깔끔해서 좋음"));

        return ResponseEntity.ok(summary);
    }


}
