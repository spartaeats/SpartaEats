package com.sparta.sparta_eats.item.presentation.dto.response;

import com.sparta.sparta_eats.item.domain.entity.ItemOption;

import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResItemOptionDtoV1 {

	private UUID id;

	private String name;

	private Integer optionType;

	private String optionTypeDescription;

	private BigInteger addPrice;

	private UUID itemId;

	private String itemName;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private String createdBy;

	private String updatedBy;

	public static ResItemOptionDtoV1 from(ItemOption itemOption) {
		return ResItemOptionDtoV1.builder()
			.id(itemOption.getId())
			.name(itemOption.getName())
			.optionType(itemOption.getOptionType())
			.optionTypeDescription(getOptionTypeDescription(itemOption.getOptionType()))
			.addPrice(itemOption.getAddPrice())
			.itemId(itemOption.getItem().getId())
			.itemName(itemOption.getItem().getName())
			.createdAt(itemOption.getCreatedAt())
			.updatedAt(itemOption.getUpdatedAt())
			.createdBy(itemOption.getCreatedBy())
			.updatedBy(itemOption.getUpdatedBy())
			.build();
	}

	private static String getOptionTypeDescription(Integer optionType) {
		if (optionType == null) {
			return "Unknown";
		}

		return optionType == 0 ? "Yes" : "No";
	}
}