package com.sparta.sparta_eats.ai.application.service;

import com.sparta.sparta_eats.ai.infrastructure.api.GeminiApiClient;
import com.sparta.sparta_eats.ai.infrastructure.api.dto.SuggestCommentRequest;
import com.sparta.sparta_eats.ai.infrastructure.api.dto.SuggestItemDescriptionRequest;
import com.sparta.sparta_eats.ai.presentation.dto.request.ReviewSummaryRequest;
import com.sparta.sparta_eats.ai.presentation.dto.response.AiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {
    private final GeminiApiClient geminiApiClient;

    @Async
    public CompletableFuture<AiResponse> getReviewSummary(ReviewSummaryRequest request) {
        log.info("current Thread: {}", Thread.currentThread().getName());

        return geminiApiClient.reviewSummary(request.reviewList())
                .map(summary -> AiResponse.builder()
                        .response(summary)
                        .build())
                .toFuture();
    }

    @Async
    public CompletableFuture<AiResponse> getSuggestedDescription(SuggestItemDescriptionRequest request) {
        return geminiApiClient.suggestItemDescription(request)
                .map(summary -> AiResponse.builder()
                        .response(summary)
                        .build())
                .toFuture();
    }

    @Async
    public CompletableFuture<AiResponse> getSuggestedComment(SuggestCommentRequest request) {
        return geminiApiClient.suggestComment(request)
                .map(summary -> AiResponse.builder()
                        .response(summary)
                        .build())
                .toFuture();
    }
}
