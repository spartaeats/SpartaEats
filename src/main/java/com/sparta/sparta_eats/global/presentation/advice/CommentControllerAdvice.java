package com.sparta.sparta_eats.global.presentation.advice;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sparta.sparta_eats.global.domain.exception.CommonException;
import com.sparta.sparta_eats.global.presentation.dto.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice("com.sparta.sparta_eats")
public class CommentControllerAdvice {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> errorHandler(Exception e) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 기본 500
		Object message = e.getMessage();

		if (e instanceof CommonException commonException) { // 직접 정의한 예외 체계
			status = commonException.getStatus();
			Map<String, List<String>> errorMessages = commonException.getErrorMessages(); // 요청 데이터 검증 실패 메시지
			if (errorMessages != null) {
				message = errorMessages;
			}
		}

		// log.error

		return ResponseEntity
			.status(status)
			.body(new ErrorResponse(status, message));
	}
}
