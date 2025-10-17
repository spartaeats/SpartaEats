package com.sparta.sparta_eats.ai.presentation;

import com.sparta.sparta_eats.ai.application.service.AiService;
import com.sparta.sparta_eats.ai.infrastructure.api.dto.SuggestCommentRequest;
import com.sparta.sparta_eats.ai.infrastructure.api.dto.SuggestItemDescriptionRequest;
import com.sparta.sparta_eats.ai.presentation.dto.request.ReviewSummaryRequest;
import com.sparta.sparta_eats.ai.presentation.dto.response.AiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/ai")
public class AiController {
    private final AiService aiService;

    @PostMapping("/reviews/summarize")
    public CompletableFuture<ResponseEntity<AiResponse>> summaryReviews(@RequestBody ReviewSummaryRequest request) {
        return aiService.getReviewSummary(request)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError()
                        .body(AiResponse.builder()
                                .response(ex.getMessage())
                                .build()));

    }

    @PostMapping("/items/suggest-description")
    public CompletableFuture<ResponseEntity<AiResponse>> suggestItemDescription(@RequestBody SuggestItemDescriptionRequest request) {
        return aiService.getSuggestedDescription(request)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError()
                        .body(AiResponse.builder()
                                .response(ex.getMessage())
                                .build()));
    }

    @PostMapping("/comments/suggest-reply")
    public CompletableFuture<ResponseEntity<AiResponse>> suggestReply(@RequestBody SuggestCommentRequest request) {
        return aiService.getSuggestedComment(request)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError()
                        .body(AiResponse.builder()
                                .response(ex.getMessage())
                                .build()));
    }
}
