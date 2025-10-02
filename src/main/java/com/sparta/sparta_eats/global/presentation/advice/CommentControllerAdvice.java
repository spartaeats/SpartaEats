package com.sparta.sparta_eats.global.presentation.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sparta.sparta_eats.global.presentation.dto.ErrorResponse;

@RestControllerAdvice("com.sparta.sparta_eats")
public class CommentControllerAdvice {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e) {

		return null;
	}
}
