package com.sparta.sparta_eats.cart.application.exception;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException() { super("Cart not found"); }
}
