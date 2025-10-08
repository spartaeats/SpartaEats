package com.sparta.sparta_eats.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.sparta_eats.ai.infrastructure.api.GeminiApiClient;
import com.sparta.sparta_eats.ai.infrastructure.api.dto.FindStoresRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.IntStream;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class AiUnitTest {
    private GeminiApiClient client;

    @Test
    void 리뷰_요약_테스트() {
        String response = client.reviewSummary(List.of("순대국이 짜다", "순대가 당면 순대임 미친", "소주가 비쌈", "순대 꽉찬게 맛있음", "머릿고기 너무 쫄깃해서 좋음", "포장 깔끔해서 좋음"));

        System.out.println(response);
    }

    @Test
    void 키워드_분석_매장_조회_테스트() {
        final List<String> REVIEW_POOL = List.of(
                // 긍정적 리뷰
                "짜장면이 옛날 맛 그대로네요. 정말 맛있습니다.",
                "짬뽕 국물이 정말 얼큰하고 시원해요. 해물도 신선하고요.",
                "탕수육은 튀김옷이 얇고 고기가 꽉 차 있어서 최고예요. 소스도 완벽!",
                "볶음밥이 고슬고슬하고 불맛이 살아있어요. 인생 볶음밥입니다.",
                "가성비가 정말 최고입니다. 양이 엄청 많아서 배부르게 먹었어요.",
                "배달이 예상 시간보다 훨씬 빨리 도착했어요. 음식도 따뜻하고요.",
                "포장을 정말 꼼꼼하게 해주셨네요. 국물 한 방울 안 샜어요.",
                "사장님이신지 직원분이신지 정말 친절하셔서 기분 좋게 식사했습니다.",
                "매장이 깔끔하고 넓어서 단체로 가기에도 좋을 것 같아요.",

                // 부정적 리뷰
                "짜장면이 너무 달고 느끼해요. 제 입맛에는 안 맞네요.",
                "짬뽕에 해물이 너무 적게 들어있어요. 가격에 비해 아쉽습니다.",
                "탕수육이 너무 딱딱하고 고기에서 냄새가 나는 것 같아요.",
                "배달이 한 시간 넘게 걸려서 면이 다 불어서 왔어요.",
                "음식이 거의 다 식어서 도착했습니다. 다시 데워 먹었네요.",
                "주문한 메뉴가 누락되었어요. 전화 응대도 불친절했고요.",
                "위생 상태가 좀 의심스러워요. 식기에서 냄새가 났습니다.",

                // 복합적 리뷰
                "탕수육은 정말 맛있는데, 같이 시킨 짜장면은 너무 평범했어요.",
                "맛은 있는데 양이 너무 적어서 아쉬웠어요. 성인 남성은 곱빼기 필수입니다.",
                "음식은 괜찮았지만 배달이 너무 늦어서 다음엔 그냥 방문해서 먹으려고요."
        );

        Random random = new Random();
        List<FindStoresRequest> requests = IntStream.range(0, 10)
                .mapToObj(i -> {
                    // Pool을 복사하여 섞은 후, 일부만 사용해 무작위성을 높임
                    List<String> shuffledReviews = new ArrayList<>(REVIEW_POOL);
                    Collections.shuffle(shuffledReviews);

                    // 각 요청마다 2~4개의 무작위 리뷰를 선택
                    int reviewCount = random.nextInt(3) + 2; // 2, 3, 4 중 하나
                    List<Object> selectedReviews = new ArrayList<>(shuffledReviews.subList(0, reviewCount));

                    return FindStoresRequest.builder()
                            .storeId(UUID.randomUUID())
                            .reviewList(selectedReviews)
                            .build();
                })
                .toList();

        List<UUID> res = new ArrayList<>();
        requests.forEach(request -> {
            try {
                boolean response = client.getStoresWithKeyword("매운 짬뽕", request);
                if (response) {
                    res.add(request.storeId());
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        res.forEach(System.out::println);
    }
}
