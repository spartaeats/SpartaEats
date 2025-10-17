package com.sparta.sparta_eats.item.presentation.dto.request;

import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqItemOptionCreateDtoV1 {

	private String name;

	private Integer optionType;

	private BigInteger addPrice;
}