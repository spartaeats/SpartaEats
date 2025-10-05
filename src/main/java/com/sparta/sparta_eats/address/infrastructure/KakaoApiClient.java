package com.sparta.sparta_eats.address.infrastructure;

import com.sparta.sparta_eats.address.domain.entity.Coordinate;
import com.sparta.sparta_eats.address.presentation.dto.response.KakaoCoordinateResponse;
import com.sparta.sparta_eats.global.domain.exception.BadRequestException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.net.URISyntaxException;

@Component
public class KakaoApiClient {
    private String apiKey;

    public Coordinate loadCoordinate(String addrRoad) throws URISyntaxException {
        RestClient client = RestClient.create();

        KakaoCoordinateResponse response = client
                .get()
                .uri(uriBuilder -> uriBuilder.path("https://dapi.kakao.com/v2/local/search/address.json")
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
                .addrLat(new BigDecimal(document.getY()))
                .addrLng(new BigDecimal(document.getX()))
                .build();
    }
}
