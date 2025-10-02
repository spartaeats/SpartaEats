package com.sparta.sparta_eats.global.presentation.dto;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ErrorResponse {
	private HttpStatus status;
	private String message;
}
