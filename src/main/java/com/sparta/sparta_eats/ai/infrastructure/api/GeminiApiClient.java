package com.sparta.sparta_eats.ai.infrastructure.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.sparta.sparta_eats.ai.infrastructure.api.dto.FindStoresRequest;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class GeminiApiClient {
    @Value("${google.api.key}")
    private String apiKey;
    private Client client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
         client = Client.builder().apiKey(apiKey).build();
    }

    public String reviewSummary(List<String> reviewList) {
        StringBuilder builder = new StringBuilder();
        reviewList.forEach(builder::append);
        String reviews = builder.toString();

        String prompt = "당신은 고객의 리뷰를 분석하고 핵심을 요약하는 전문가이다." +
                "아래에 전달되는 리뷰 목록을 바탕으로, 다음 형식에 맞춰 한국어로 종합적인 요약을 생성할 것." +
                "1.  **총평**: 전체적인 리뷰의 긍정/부정 뉘앙스와 고객들의 핵심적인 반응을 1~2문장으로 요약할 것." +
                "2.  **긍정적인 점**: 고객들이 공통적으로 칭찬하는 부분들을 구체적인 항목으로 정리할 것.. (예: 맛, 포장 상태 등)" +
                "3.  **아쉬운 점**: 고객들이 공통적으로 불만이나 개선을 요구하는 부분들을 구체적인 항목으로 정리할 것."+
                "4.  **핵심 키워드**: 리뷰들에서 가장 자주 언급되는 키워드를 3~5개 뽑아줄 것." +
                "리뷰 목록: " + reviews;

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        prompt,
                        null);

        return response.text();
    }

    public boolean getStoresWithKeyword(String keyword, FindStoresRequest request) throws JsonProcessingException {
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
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        prompt,
                        null);

        return Boolean.parseBoolean(response.text());
    }

    // TODO 파라미터로 상품 리스트를 받아야함
    public String getIllegalReviewsId(List<String> reviewList) {
        StringBuilder builder = new StringBuilder();
        reviewList.forEach(builder::append);
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

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        prompt,
                        null);

        return response.text();
    }
}
