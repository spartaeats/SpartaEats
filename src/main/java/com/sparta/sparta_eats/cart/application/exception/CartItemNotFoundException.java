package com.sparta.sparta_eats.cart.application.exception;

/**
 * 장바구니 아이템을 찾을 수 없을 때 발생하는 예외
 * HTTP 404 응답으로 매핑됨
 */
public class CartItemNotFoundException extends RuntimeException {
    
    public CartItemNotFoundException() {
        super("cart item not found");
    }
    
    public CartItemNotFoundException(String message) {
        super(message);
    }
}
