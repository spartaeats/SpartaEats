package com.sparta.sparta_eats.global.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.math.BigDecimal;

/**
 * 금액을 "원 단위 정수"로 DB에 저장하기 위한 컨버터.
 * Java: BigDecimal(scale=0)  <->  DB: BIGINT
 */
@Converter(autoApply = false) // 필요한 필드에만 명시적으로 적용 (@Convert)
public class MoneyLongConverter implements AttributeConverter<BigDecimal, Long> {

    @Override
    public Long convertToDatabaseColumn(BigDecimal attribute) {
        if (attribute == null) return null;

        // 스케일 강제: 소수점 금지 (원 단위)
        // stripTrailingZeros()로 100.00 같은 경우 100으로 허용
        if (attribute.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("금액(BigDecimal)은 소수점 없이 원 단위여야 합니다: " + attribute);
        }

        // 음수 금지 등 정책이 필요하면 아래 체크 추가
        if (attribute.signum() < 0) {
            throw new IllegalArgumentException("금액은 음수일 수 없습니다: " + attribute);
        }

        // 범위 및 소수점 위반을 명확히 검출
        return attribute.longValueExact();
    }

    @Override
    public BigDecimal convertToEntityAttribute(Long dbData) {
        if (dbData == null) return null;
        return BigDecimal.valueOf(dbData); // scale=0
    }
}