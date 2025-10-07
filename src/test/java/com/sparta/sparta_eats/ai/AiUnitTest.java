package com.sparta.sparta_eats.ai;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.junit.jupiter.api.Test;

public class AiUnitTest {
    @Test
    void initTest() {
        String apiKey = "AIzaSyA6uV_cNdXcyqvnQy2hTqx25e7mzvg2Bfc";

        Client client = new Client.Builder().apiKey(apiKey).build();

        String reviews = "순대국이 짜다 \n순대가 당면 순대임 미친\n소주가 비쌈\n순대 꽉찬게 맛있음\n머릿고기 너무 쫄깃해서 좋음\n포장 깔끔해서 좋음";

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

        System.out.println(response.text());
    }
}
