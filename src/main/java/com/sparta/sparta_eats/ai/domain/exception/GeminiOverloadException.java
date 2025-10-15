package com.sparta.sparta_eats.ai.domain.exception;

import com.sparta.sparta_eats.global.domain.exception.CommonException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class GeminiOverloadException extends CommonException {

    public GeminiOverloadException(String message, HttpStatus status) {
        super(message, status);
    }

    public GeminiOverloadException(Map<String, List<String>> errorMessages, HttpStatus status) {
        super(errorMessages, status);
    }
}
