package com.sparta.sparta_eats.address.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.sparta.sparta_eats.address.domain.entity.Coordinate;
import com.sparta.sparta_eats.address.presentation.dto.request.TmapTimeRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

@Component
public class TmapApiClient {
    @Value("${tmap.api-key}")
    private String apiKey;
    private final RestClient client = RestClient.builder()
            .baseUrl("https://apis.openapi.sk.com")
            .build();

    public int getDistance(Coordinate start, Coordinate target) {
        JsonNode response = client.get()
                .uri(uriBuilder -> uriBuilder.path("/tmap/routes/distance")
                        .queryParam("version", 1)
                        .queryParam("startX", start.getAddrLng())
                        .queryParam("startY", start.getAddrLat())
                        .queryParam("endX", target.getAddrLng())
                        .queryParam("endY", target.getAddrLat())
                        .build())
                .header("appKey", apiKey)
                .retrieve()
                .toEntity(JsonNode.class)
                .getBody();

        assert response != null;
        return response.path("distanceInfo").path("distance").asInt();
    }

    public int getTime(Coordinate start, Coordinate target) {
        TmapTimeRequest request = TmapTimeRequest.builder()
                .startX(start.getAddrLng())
                .startY(start.getAddrLat())
                .endX(target.getAddrLng())
                .endY(target.getAddrLat())
                .build();

        JsonNode response = client.post()
                .uri(uriBuilder -> uriBuilder.path("/tmap/routes")
                        .queryParam("version", 1)
                        .build())
                .header("appKey", apiKey)
                .body(request)
                .retrieve()
                .toEntity(JsonNode.class)
                .getBody();

        assert response != null;
        return response.path("features").path(0).path("properties").path("totalTime").asInt() / 60;
    }
}
