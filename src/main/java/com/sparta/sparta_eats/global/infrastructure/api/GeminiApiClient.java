package com.sparta.sparta_eats.global.infrastructure.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.sparta.sparta_eats.global.infrastructure.api.dto.FindStoresRequest;
import com.sparta.sparta_eats.global.infrastructure.api.dto.GeminiResponse;
import com.sparta.sparta_eats.global.infrastructure.api.dto.SuggestCommentRequest;
import com.sparta.sparta_eats.global.infrastructure.api.dto.SuggestItemDescriptionRequest;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class GeminiApiClient {
    @Value("${google.api.key}")
    private String apiKey;
    private final WebClient webClient;
    private Client client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        client = Client.builder().apiKey(apiKey).build();
    }


    public GeminiApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader("x-goog-api-key", apiKey)
                .build();
    }

    private Map<String, Object> createRequestBody(String prompt) {
        return Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                ))
        );
    }

    public Mono<String> reviewSummary(List<String> reviewList) {
        StringBuilder builder = new StringBuilder();
        reviewList.forEach(builder::append);
        String reviews = builder.toString();

        String prompt = "당신은 고객 리뷰 분석 전문가입니다. " +
                "아래 리뷰 목록을 바탕으로, 전반적인 긍정/부정 평가, 칭찬 요소와 " +
                "불만 요소를 모두 포함하여 " +
                "하나의 완결된 문단으로 요약된 1~2 문장의 최종 결과물만 생성하세요. " +
                "**중요: 마크다운, 글머리 기호(*, -), 번호 매기기, 별도의 항목 분류를 절대 사용하지 말고, 오직 줄 바꿈 없는 순수 텍스트로만 응답해야 합니다.** " +
                "리뷰: " + reviews;

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-2.5-flash:generateContent")
                        .queryParam("key", apiKey)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequestBody(prompt))
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .map(geminiResponse -> geminiResponse.candidates().get(0).content().parts().get(0).text());
    }

    public Mono<Boolean> getStoresWithKeyword(String keyword, FindStoresRequest request) throws JsonProcessingException {
        String prompt = "당신은 고객의 특정 키워드와 수많은 매장 리뷰 데이터를 분석하여, 고객의 요구에 가장 부합하는 매장을 찾아 추천하는 '매장 추천 전문가'이다.\n" +
                "\n" +
                "주어진 [사용자 키워드]와 [매장별 리뷰 데이터]를 면밀히 분석하세요.\n" +
                "각 매장의 리뷰들이 사용자의 키워드와 얼마나 관련이 깊은지를 평가하여, 키워드가 알맞은 매장인지를 판단하여야 한다.\n" +
                "\n" +
                "결과는 반드시 boolean 타입의 true 혹은 false를 반환해야 한다.\n" +
                "\n" +
                "---\n" +
                "\n" +
                "[사용자 키워드]\n" +
                keyword +
                "\n" +
                "---\n" +
                "\n" +
                "[매장별 리뷰 데이터]\n" +
                objectMapper.writeValueAsString(request.reviewList()) +
                "\n" +
                "---\n" +
                "\n" +
                "// 만약 키워드에 부합하는 매장이 없다면, false를 응답할 것.\n";

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-2.5-flash:generateContent")
                        .queryParam("key", apiKey)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequestBody(prompt))
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .map(geminiResponse -> Boolean.parseBoolean(geminiResponse.candidates().get(0).content().parts().get(0).text()));
    }

    // TODO 파라미터로 상품 리스트를 받아야함
    public Mono<UUID> getIllegalReviewsId(List<String> reviewList) {
        StringBuilder builder = new StringBuilder();
        reviewList.forEach(content -> {
            builder.append(content);
            builder.append("\n");
        });
        String totalReviews = builder.toString();

        String prompt = "당신은 주어진 정책에 따라 부적절한 내용을 필터링하는 AI 시스템입니다.\n" +
                "\n" +
                "아래 [리뷰 운영 정책]을 기준으로, [검토할 리뷰 목록]에 있는 각 리뷰를 검토하세요.\n" +
                "정책을 위반한 리뷰의 **ID만** 정확히 추출하여, 쉼표로 구분된 목록 형태로 응답해야 합니다.\n" +
                "\n" +
                "만약 정책을 위반한 리뷰가 단 하나도 없다면, 결과는 반드시 '없음'이라고만 출력하세요. 다른 설명은 추가하지 마세요.\n" +
                "\n" +
                "---\n" +
                "\n" +
                "[리뷰 운영 정책]\n" +
                "1.  **개인정보 포함**: 이름, 전화번호, 주소, 이메일 등 식별 가능한 개인정보.\n" +
                "2.  **욕설 및 혐오 발언**: 비속어, 타인을 비방하거나 모욕하는 내용, 차별적인 언어.\n" +
                "3.  **스팸 및 광고**: 상품과 무관한 광고, 홍보성 링크, 의미 없는 문자 반복.\n" +
                "4.  **주제와 무관한 내용**: 상품이나 서비스와 전혀 관련 없는 내용.\n" +
                "\n" +
                "---\n" + totalReviews;

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-2.5-flash:generateContent")
                        .queryParam("key", apiKey)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequestBody(prompt))
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .map(geminiResponse -> UUID.fromString(geminiResponse.candidates().get(0).content().parts().get(0).text()));
    }

    public Mono<String> suggestItemDescription(SuggestItemDescriptionRequest request) {
        String prompt = "너는 지금부터 대한민국 최고의 배달 음식 전문 카피라이터이다. 사장님이 입력한 아래의 최소 정보를 가지고, 고객의 침샘을 자극하고 '주문하기' 버튼을 누르게 만들 매력적인 메뉴 설명을 생성해야 한다.\n" +
                "- 메뉴이름: " + request.name() +
                "- 간단 설명: " + request.simpleDescription() +
                "- 핵심 특징: " + request.feature() + "\n" +
                "역할 부여: 너의 역할은 단순히 정보를 나열하는 것이 아니라, \"음식을 파는 마케터\"의 입장에서 글을 쓰는 것이다.\n" +
                "타겟 설정: 이 글을 읽는 사람은 배고픈 상태로 배달 앱을 보고 있다. 시각, 미각, 청각을 자극하는 표현을 사용해 식욕을 최대한 끌어올려라.\n" +
                "톤앤매너: 친근하고, 유머러스하며, 강한 자신감이 느껴지는 말투를 사용하라.\n" +
                "글의 형식: 2~3문장 정도의 짧고 임팩트 있는 단락으로 완성하라. 고객들은 긴 글을 읽지 않는다.\n" +
                "금지 사항: 어려운 단어나 애매한 표현은 사용하지 마라. 누구나 쉽고 직관적으로 이해할 수 있어야 한다.";

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-2.5-flash:generateContent")
                        .queryParam("key", apiKey)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequestBody(prompt))
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .map(geminiResponse -> geminiResponse.candidates().get(0).content().parts().get(0).text());
    }

    public Mono<String> suggestComment(SuggestCommentRequest request) {
        StringBuilder builder = new StringBuilder();
        request.itemList().forEach(item -> {
            builder.append(item);
            builder.append(", ");
        });
        String totalItem = builder.toString();

        String prompt = "너는 지금부터 [가게 이름]의 노련한 고객 관리 매니저다. 고객이 남긴 소중한 리뷰를 분석하여, 우리 가게의 이미지를 긍정적으로 만들고 단골 고객을 확보할 수 있는 진심 어린 답글을 작성해야 한다.\n" +
                "가게 이름: " + request.storeName() +
                "\n" +
                "고객 닉네임: " + request.nickname() +
                "\n" +
                "별점: " + request.rate() +
                "\n" +
                "리뷰 원문: " + request.content() +
                "\n" +
                "주문 메뉴: " + totalItem +
                "\n" +
                "답글 톤앤매너: 친근하고 따뜻하게\n" +
                "지시사항 및 행동 강령\n" +
                "1단계: 리뷰 종류 자동 분석\n" +
                "\n" +
                "별점 7-10점 & 긍정적 단어 (맛있다, 최고, 친절하다 등) 포함 시 → '긍정 리뷰'로 판단.\n" +
                "\n" +
                "별점 2-5점 & 부정적 단어 (최악, 늦었다, 식었다, 맛없다 등) 포함 시 → '부정 리뷰'로 판단.\n" +
                "\n" +
                "별점 6점 또는 긍정/부정 혼재 시 → '개선 요청 리뷰'로 판단.\n" +
                "\n" +
                "내용이 거의 없는 짧은 리뷰 (잘먹었어요 등) → '간단 리뷰'로 판단.\n" +
                "\n" +
                "2단계: 분석 결과에 따른 맞춤 답글 생성 (행동 강령)\n" +
                "\n" +
                "A. '긍정 리뷰' 대응 매뉴얼:\n" +
                "\n" +
                "고객 호명: [고객 닉네임]님! 으로 시작하여 친밀감을 표현한다.\n" +
                "\n" +
                "구체적 감사: \"소중한 시간 내어\" \"정성스러운 리뷰\" 등 감사의 마음을 구체적으로 표현한다.\n" +
                "\n" +
                "리뷰 내용 언급: 고객이 칭찬한 부분(예: '치킨이 바삭해서 좋았다')이나 주문한 메뉴([주문 메뉴])를 정확히 언급하며 공감대를 형성한다.\n" +
                "\n" +
                "약속: \"늘 최고의 맛을 유지하도록\" \"더 큰 만족을 드리도록\" 등 앞으로의 포부를 밝힌다.\n" +
                "\n" +
                "재주문 유도: \"다음에 또 찾아주세요!\" 등 따뜻한 끝인사로 마무리한다.\n" +
                "\n" +
                "B. '부정 리뷰' 대응 매뉴얼 (가장 중요):\n" +
                "\n" +
                "고객 호명 및 즉각적 사과: [고객 닉네임]님. 으로 시작하며, 변명 없이 \"먼저 불편을 드려 진심으로 죄송합니다.\"로 사과부터 한다.\n" +
                "\n" +
                "문제점 인정: 고객이 지적한 문제(예: 배달 지연, 음식이 식은 점)를 회피하지 않고 그대로 인정하고 공감한다. (\"얼마나 속상하셨을지...\")\n" +
                "\n" +
                "원인 파악 및 개선 약속: \"말씀해주신 부분은 저희가 꼭 확인하여...\" 와 같이 개선 의지를 명확하고 구체적으로 보여준다.\n" +
                "\n" +
                "보상 제안 (선택적): \"다음 주문 시 요청사항에 남겨주시면...\" 과 같이 보상을 제안하여 고객의 마음을 되돌릴 기회를 만든다. (이 부분은 사장님이 선택할 수 있는 옵션으로 제공)\n" +
                "\n" +
                "기회 요청: \"염치없지만 한 번 더 기회를 주신다면...\" 이라는 정중한 표현으로 재주문을 부탁한다.\n" +
                "\n" +
                "C. '개선 요청' 및 '간단 리뷰' 대응 매뉴얼:\n" +
                "\n" +
                "감사 표현: 긍정적인 부분에 대해 먼저 감사한다.\n" +
                "\n" +
                "피드백 수용: 지적된 부분은 겸허히 수용하고 개선을 약속한다. (\"말씀 덕분에 저희가 한 단계 더 발전할 수 있습니다.\")\n" +
                "\n" +
                "짧은 리뷰라도 \"맛있게 드셨다니 저희가 더 기쁩니다!\" 와 같이 따뜻한 감정을 담아 답글을 작성한다.\n" +
                "\n";

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-2.5-flash:generateContent")
                        .queryParam("key", apiKey)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequestBody(prompt))
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .map(geminiResponse -> geminiResponse.candidates().get(0).content().parts().get(0).text());
    }
}
