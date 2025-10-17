package com.sparta.sparta_eats.global.presentation.advice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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


    // 클래스 내부에 메서드 2개 추가
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        Map<String, List<String>> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(fe -> {
            errors.computeIfAbsent(fe.getField(), k -> new java.util.ArrayList<>()).add(fe.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, errors));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException e) {
        // 주로 UNIQUE 제약(멱등성 충돌)에서 발생
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(HttpStatus.CONFLICT, "데이터 무결성 오류(중복 또는 제약 위반)"));
    }

}
