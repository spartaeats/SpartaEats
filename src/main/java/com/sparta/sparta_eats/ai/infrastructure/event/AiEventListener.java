package com.sparta.sparta_eats.ai.infrastructure.event;

import com.sparta.sparta_eats.ai.domain.event.ReviewSummaryEvent;
import com.sparta.sparta_eats.ai.infrastructure.api.GeminiApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiEventListener {
    private final GeminiApiClient geminiApiClient;
}
