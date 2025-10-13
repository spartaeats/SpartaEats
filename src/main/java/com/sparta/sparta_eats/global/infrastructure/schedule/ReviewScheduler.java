package com.sparta.sparta_eats.global.infrastructure.schedule;

import com.sparta.sparta_eats.global.infrastructure.api.GeminiApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// TODO 우선은 global 디렉토리에 두지만 Review merge 이후 directory 에 따로 생성해야함
@Component
@RequiredArgsConstructor
public class ReviewScheduler {
    private final GeminiApiClient geminiApiClient;

    @Scheduled(cron = "0 0 0 * * *")
    public void removeIllegalReviews() {
        // TODO ReviewRepository에서 전체 리뷰 리스트를 뽑아와야함.
        List<String> reviewList = new ArrayList<>();
        Mono<UUID> response = geminiApiClient.getIllegalReviewsId(reviewList);
    }
}
