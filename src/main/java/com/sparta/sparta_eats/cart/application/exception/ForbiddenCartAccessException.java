package com.sparta.sparta_eats.cart.application.exception;

public class ForbiddenCartAccessException extends RuntimeException {
    public ForbiddenCartAccessException() { super("Forbidden: cart ownership mismatch"); }
}
