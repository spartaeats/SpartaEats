package com.sparta.sparta_eats.address.infrastructure.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.sparta.sparta_eats.address.domain.entity.Coordinate;
import com.sparta.sparta_eats.address.presentation.dto.request.TmapTimeRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class TmapApiClient {
    @Value("${tmap.api-key}")
    private String apiKey;
    private final RestClient client = RestClient.builder()
            .baseUrl("https://apis.openapi.sk.com")
            .build();

//    public int getDistance(Coordinate start, Coordinate target) {
//        JsonNode response = client.get()
//                .uri(uriBuilder -> uriBuilder.path("/tmap/routes/distance")
//                        .queryParam("version", 1)
//                        .queryParam("startX", start.getAddrLng())
//                        .queryParam("startY", start.getAddrLat())
//                        .queryParam("endX", target.getAddrLng())
//                        .queryParam("endY", target.getAddrLat())
//                        .build())
//                .header("appKey", apiKey)
//                .retrieve()
//                .toEntity(JsonNode.class)
//                .getBody();
//
//        assert response != null;
//        return response.path("distanceInfo").path("distance").asInt();
//    }

    public int getDistance(Coordinate start, Coordinate target) {
        double x1 = start.getAddrLng().doubleValue();
        double x2 = target.getAddrLng().doubleValue();
        double y1 = start.getAddrLat().doubleValue();
        double y2 = target.getAddrLat().doubleValue();

        double distance;
        double radius = 6371; // 지구 반지름(km)
        double toRadian = Math.PI / 180;

        double deltaLatitude = Math.abs(x1 - x2) * toRadian;
        double deltaLongitude = Math.abs(y1 - y2) * toRadian;

        double sinDeltaLat = Math.sin(deltaLatitude / 2);
        double sinDeltaLng = Math.sin(deltaLongitude / 2);
        double squareRoot = Math.sqrt(
                sinDeltaLat * sinDeltaLat +
                        Math.cos(x1 * toRadian) * Math.cos(x2 * toRadian) * sinDeltaLng * sinDeltaLng);

        distance = 2 * radius * Math.asin(squareRoot);

        return (int) distance;
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
