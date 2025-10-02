package com.sparta.sparta_eats.global.domain.exception;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class NotFoundException extends CommonException {

	public NotFoundException() {
		super("NotFound", HttpStatus.NOT_FOUND);
		setErrorCode(true);
	}
}
