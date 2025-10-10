package com.sparta.sparta_eats.address.infrastructure.api.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TmapTimeRequest(
        @NotNull(message = "{validation.coordinates.x.not_null}")
        @DecimalMin(value = "124.0", message = "{validation.coordinates.longitude.range.korea}")
        @DecimalMax(value = "132.0", message = "{validation.coordinates.longitude.range.korea}")
        BigDecimal startX,

        @NotNull(message = "{validation.coordinates.y.not_null}")
        @DecimalMin(value = "33.0", message = "{validation.coordinates.latitude.range.korea}")
        @DecimalMax(value = "39.0", message = "{validation.coordinates.latitude.range.korea}")
        BigDecimal startY,

        @NotNull(message = "{validation.coordinates.x.not_null}")
        @DecimalMin(value = "124.0", message = "{validation.coordinates.longitude.range.korea}")
        @DecimalMax(value = "132.0", message = "{validation.coordinates.longitude.range.korea}")
        BigDecimal endX,

        @NotNull(message = "{validation.coordinates.y.not_null}")
        @DecimalMin(value = "33.0", message = "{validation.coordinates.latitude.range.korea}")
        @DecimalMax(value = "39.0", message = "{validation.coordinates.latitude.range.korea}")
        BigDecimal endY
) {}