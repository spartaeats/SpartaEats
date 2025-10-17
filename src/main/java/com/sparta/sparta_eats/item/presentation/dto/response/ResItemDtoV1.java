package com.sparta.sparta_eats.item.presentation.dto.response;

import com.sparta.sparta_eats.item.domain.entity.Item;

import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResItemDtoV1 {
	private UUID id;
	private String name;
	private String description;
	private BigInteger price;
	private BigInteger salePrice;
	private String image;
	private Boolean active;
	private Boolean soldOut;
	private UUID categoryId;
	private String categoryName;
	private UUID storeId;
	private String storeName;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String createdBy;
	private String updatedBy;

	public static ResItemDtoV1 from(Item item) {
		return ResItemDtoV1.builder()
			.id(item.getId())
			.name(item.getName())
			.description(item.getDescription())
			.price(item.getPrice())
			.salePrice(item.getSalePrice())
			.image(item.getImage())
			.active(item.getActive())
			.soldOut(item.getSoldOut())
			.categoryId(item.getItemCategory().getId())
			.categoryName(item.getItemCategory().getName())
			.storeId(item.getStore().getId())
			.storeName(item.getStore().getName())
			.createdAt(item.getCreatedAt())
			.updatedAt(item.getUpdatedAt())
			.createdBy(item.getCreatedBy())
			.updatedBy(item.getUpdatedBy())
			.build();
	}
}