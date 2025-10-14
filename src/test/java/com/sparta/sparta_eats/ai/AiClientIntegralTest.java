package com.sparta.sparta_eats.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.sparta_eats.global.infrastructure.api.GeminiApiClient;
import com.sparta.sparta_eats.global.infrastructure.api.dto.FindStoresRequest;
import com.sparta.sparta_eats.global.infrastructure.api.dto.SuggestCommentRequest;
import com.sparta.sparta_eats.global.infrastructure.api.dto.SuggestItemDescriptionRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@EnableRetry
@SpringBootTest
public class AiClientIntegralTest {
    @Autowired
    GeminiApiClient client;

    @BeforeEach
    void setup() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Retryable(
            value = WebClientResponseException.TooManyRequests.class, // TooManyRequests 오류 발생 시에만
            maxAttempts = 3, // 최대 3번까지 재시도
            backoff = @Backoff(delay = 2000) // 재시도하기 전에 2초(2000ms) 대기
    )

    @Test
    void 리뷰_요약_테스트() {
        Mono<String> response = client.reviewSummary(
                List.of(
                        // 긍정적인 리뷰
                        "여기 진짜 찐맛집이네요. 국물이 엄청 진하고 깊어서 해장 제대로 했습니다. 건더기도 푸짐해서 밥 한 공기 말아먹으니 배 터지는 줄 알았어요. 앞으로 순대국은 여기 정착합니다!",
                        "순대국 특으로 시켰는데 머릿고기랑 순대가 정말 실하게 들어있네요. 잡내 하나도 안 나고 너무 부드러워요. 깍두기도 딱 알맞게 익어서 순대국이랑 환상의 조합이었습니다.",
                        "배달 시간 딱 맞춰서 따끈하게 도착했어요. 포장도 국물 한 방울 안 새게 꼼꼼하게 해주셔서 감동입니다. 양념장(다대기)도 넉넉하게 챙겨주셔서 취향껏 먹기 좋았어요.",
                        "리뷰 이벤트로 받은 찰순대가 미쳤어요. 그냥 서비스 수준이 아니라 메뉴로 팔아도 될 퀄리티입니다. 쫀득하고 속이 꽉 차서 너무 맛있게 먹었습니다. 물론 메인인 순대국도 최고였고요.",
                        "혼밥 메뉴로 이만한 게 없네요. 1인분인데도 양이 많아서 든든하게 한 끼 해결했습니다. 국물이 보약 같아요. 다 먹고 나니 몸보신한 기분입니다.",
                        "요청사항에 '공기밥 하나 더 추가'라고 적었는데 잊지 않고 챙겨주셔서 감사합니다! 늘 친절하시고 맛도 변함이 없어서 자주 시켜 먹게 돼요. 번창하세요!",
                        "와... 여기 순대는 토종순대인가요? 당면만 들어간 게 아니라 속이 꽉 찬 게 정말 맛있네요. 국물은 깔끔하면서도 깊은 맛이 일품입니다. 술안주로도 최고일 듯.",
                        "포장이 정말 깔끔해서 마음에 들어요. 밥, 국, 반찬 전부 개별 용기에 담아주셔서 먹기도 편하고 뒤처리도 쉬웠습니다. 맛은 두말하면 잔소리죠.",
                        "아이가 순대국을 처음 먹어보는데, 맵지 않은 하얀 국물로 달라고 부탁드렸더니 정말 잘 챙겨주셨어요. 아이가 맛있다며 한 그릇 뚝딱 비웠습니다. 감사합니다!",
                        "모듬 수육 중짜리 시켜서 반주했는데, 고기가 야들야들하고 부위별로 다양하게 와서 골라 먹는 재미가 있었어요. 같이 온 국물 서비스도 진국이네요.",
                        "배달 기사님도 친절하시고, 음식도 항상 따뜻하게 와서 만족스럽습니다. 꾸준히 시켜 먹는 단골집이에요.",
                        "순대국 국물이 진짜 사골을 푹 고아낸 맛이에요. 인스턴트 맛 전혀 안 나고 건강한 맛이라 부모님도 좋아하셨습니다.",
                        "깍두기랑 김치가 신의 한 수입니다. 적당히 익어서 시원하고 아삭한 맛이 순대국의 느끼함을 싹 잡아줘요. 리필하고 싶을 정도입니다.",
                        "가격 대비 양이 정말 푸짐합니다. 요즘 물가에 이 정도 퀄리티와 양이라니, 사장님 남는 게 있으신가요? 늘 감사히 먹고 있습니다.",
                        "얼큰 순대국 시켰는데, 너무 맵기만 한 게 아니라 맛있게 칼칼해서 땀 흘리면서 시원하게 먹었습니다. 스트레스가 확 풀리는 맛이에요!",

                        // 부정적인 리뷰
                        "오늘따라 국물이 너무 짜네요. 물을 좀 부어서 먹긴 했는데, 원래 안 이랬던 것 같은데... 주방 컨디션에 따라 맛이 조금씩 다른가 봐요.",
                        "순대가 너무 당면 순대 위주로만 들어있어서 아쉬웠어요. 예전에는 피순대나 토종순대도 섞여 있었던 것 같은데... 순대 종류가 바뀐 건가요?",
                        "배달이 예정 시간보다 30분이나 늦게 도착했어요. 다 식어서 다시 데워 먹었습니다. 바쁘신 건 알겠지만 배달 시간은 좀 지켜주셨으면 좋겠네요.",
                        "포장 용기 하나가 살짝 깨져서 국물이 비닐봉지 안에 다 샜어요. 먹기 전부터 기분이 별로였습니다. 포장에 조금 더 신경 써주세요.",
                        "건더기가 예전보다 줄어든 느낌이에요. 특으로 시켰는데 일반이랑 별 차이를 모르겠네요. 가격은 올랐는데 양이 줄어서 속상합니다.",
                        "김치가 너무 쉬어서 제 입맛에는 맞지 않았습니다. 거의 묵은지 수준이라... 겉절이나 덜 익은 김치를 좋아하시는 분들은 참고하세요.",
                        "리뷰 이벤트 참여했는데 서비스가 누락됐어요. 바쁘셔서 잊으신 것 같긴 한데, 그래도 주문 확인은 꼼꼼히 해주셨으면 합니다.",
                        "머릿고기에서 약간 돼지 냄새가 나네요. 제가 좀 예민한 편이라 그럴 수도 있지만, 이전에는 안 났던 냄새라 조금 거북했습니다.",
                        "공기밥 상태가 좀... 밥을 한 지 오래됐는지 겉이 말라있고 푸석푸석했어요. 국밥집은 밥맛도 중요한데 실망입니다.",
                        "국물이 너무 기름져요. 위에 뜬 기름을 좀  걷어내고 먹었습니다. 담백한 맛을 기대했는데 생각보다 느끼해서 아쉬웠습니다.",

                        // 복합적인 리뷰
                        "맛은 있는데 너무 불친절하시네요. 배달 요청사항 관련해서 전화 문의드렸는데, 바쁘신지 귀찮다는 듯이 대답하셔서 좀 그랬습니다. 음식은 맛있어서 별 3개 드려요.",
                        "순대국 자체는 정말 맛있고 양도 푸짐해요. 그런데 같이 온 깍두기가 너무 달아요. 설탕을 너무 많이 넣으신 듯... 단맛만 좀 줄이면 완벽할 것 같습니다.",
                        "배달은 엄청 빨리 왔는데, 다대기를 빼달라고 요청했는데 그대로 들어있었어요. 아이랑 같이 먹으려고 했는데 결국 못 먹였네요. 요청사항 좀 잘 읽어주세요.",
                        "전체적으로 만족하지만, 순대보다 내장이나 고기 부속물이 훨씬 많네요. 저는 순대를 더 좋아해서... 순대 많이 옵션이 있으면 좋겠어요.",
                        "음식은 훌륭합니다. 다만 최소주문금액이 좀 높은 것 같아 혼자 시켜 먹기에는 부담스럽네요. 1인 세트 메뉴 같은 게 있으면 더 자주 시킬 것 같아요."
                ));
        String result = response.block();
        System.out.println("#### summary result ####\n" + result);
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
                    List<String> selectedReviews = new ArrayList<>(shuffledReviews.subList(0, reviewCount));

                    return FindStoresRequest.builder()
                            .storeId(UUID.randomUUID())
//                            .reviewList(selectedReviews)
                            .build();
                })
                .toList();

        List<UUID> res = new ArrayList<>();
        requests.forEach(request -> {
            try {
                boolean response = Boolean.TRUE.equals(client.getStoresWithKeyword("매운 짬뽕", request).block());
                if (response) {
                    res.add(request.storeId());
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        res.forEach(System.out::println);
    }

    @Test
    void 상품설명_제시() {
        SuggestItemDescriptionRequest request = SuggestItemDescriptionRequest.builder()
                .name("돈까스")
                .feature("바삭바삭한 튀김옷, 육즙 가득한 등심")
                .simpleDescription("가성비 좋은 돈까스")
                .build();

        System.out.println(client.suggestItemDescription(request).block());
    }

    @Test
    void 리뷰답글_제시() {
        SuggestCommentRequest request = SuggestCommentRequest.builder()
                .rate(3)
                .content("맛있는데 순대가 당면순대였음")
                .nickname("순대국킬러")
                .storeName("할매순대국")
                .itemList(List.of("순대국", "모듬순대"))
                .build();

        System.out.println(client.suggestComment(request).block());
    }
}
