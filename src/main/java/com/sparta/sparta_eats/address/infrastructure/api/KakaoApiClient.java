package com.sparta.sparta_eats.address.infrastructure.api;

import com.sparta.sparta_eats.address.domain.entity.Coordinate;
import com.sparta.sparta_eats.address.infrastructure.api.dto.response.KakaoCoordinateResponse;
import com.sparta.sparta_eats.global.domain.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class KakaoApiClient {
    @Value("${kakao.api-key}")
    private String apiKey;

    public Coordinate loadCoordinate(String addrRoad) {
        RestClient client = RestClient.builder()
                .baseUrl("https://dapi.kakao.com")
                .build();

        KakaoCoordinateResponse response = client
                .get()
                .uri(uriBuilder -> uriBuilder.path("/v2/local/search/address.json")
                        .queryParam("query", addrRoad)
                        .build())
                .header("Authorization", apiKey)
                .retrieve()
                .toEntity(KakaoCoordinateResponse.class)
                .getBody();

        if (response == null || response.documents().isEmpty())
            throw new BadRequestException("나쁜 요청");

        KakaoCoordinateResponse.Document document = response.documents().get(0);

        return Coordinate.builder()
                .addrLat(new BigDecimal(document.y()))
                .addrLng(new BigDecimal(document.x()))
                .build();
    }
}
