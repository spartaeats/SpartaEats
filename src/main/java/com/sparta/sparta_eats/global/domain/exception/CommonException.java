package com.sparta.sparta_eats.global.domain.exception;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {

	private HttpStatus status;
	private Map<String, List<String>> errorMessages; // 요청 데이터 검증 실패 메시지 - 커스텀 객체 검증 메세지
	private boolean errorCode; // 메시지 코드에 있는 메시지로 변환 필요

	// setter 직접 작성
	public void setErrorCode(boolean errorCode) {
		this.errorCode = errorCode;
	}

	public CommonException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}

	public CommonException(Map<String, List<String>> errorMessages, HttpStatus status) {
		this.status = status;
		this.errorMessages = errorMessages;
	}

}
