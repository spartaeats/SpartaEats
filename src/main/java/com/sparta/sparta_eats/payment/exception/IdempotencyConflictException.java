package com.sparta.sparta_eats.payment.exception;

import com.sparta.sparta_eats.global.domain.exception.CommonException;
import org.springframework.http.HttpStatus;

public class IdempotencyConflictException extends CommonException {
    public IdempotencyConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
