package com.sparta.sparta_eats.item.presentation.dto.request;

import lombok.*;

import java.math.BigInteger;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqItemCreateDtoV1 {
	private String name;
	private String description;
	private BigInteger price;
	private BigInteger salePrice;
	private String image;
	private Boolean active;
	private Boolean soldOut;
	private UUID categoryId;
}