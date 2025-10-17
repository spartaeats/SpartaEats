package com.sparta.sparta_eats.cart.application.exception;

/**
 * 잘못된 수량일 때 발생하는 예외
 * HTTP 400 응답으로 매핑됨
 */
public class InvalidQuantityException extends RuntimeException {
    
    public InvalidQuantityException() {
        super("invalid quantity");
    }
    
    public InvalidQuantityException(String message) {
        super(message);
    }
}
