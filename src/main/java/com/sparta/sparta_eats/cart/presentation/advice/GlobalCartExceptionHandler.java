package com.sparta.sparta_eats.cart.presentation.advice;

import com.sparta.sparta_eats.cart.application.exception.CartItemNotFoundException;
import com.sparta.sparta_eats.cart.application.exception.CartNotFoundException;
import com.sparta.sparta_eats.cart.application.exception.ForbiddenCartAccessException;
import com.sparta.sparta_eats.cart.application.exception.InvalidQuantityException;
import com.sparta.sparta_eats.cart.application.exception.StoreMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalCartExceptionHandler {

    @ExceptionHandler(StoreMismatchException.class)
    public ResponseEntity<?> handleStoreMismatch(StoreMismatchException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("code","STORE_MISMATCH","message",ex.getMessage()));
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<?> handleCartNotFound(CartNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("code","CART_NOT_FOUND","message",ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenCartAccessException.class)
    public ResponseEntity<?> handleForbidden(ForbiddenCartAccessException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("code","FORBIDDEN","message",ex.getMessage()));
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<?> handleCartItemNotFound(CartItemNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("code","CART_ITEM_NOT_FOUND","message",ex.getMessage()));
    }

    @ExceptionHandler(InvalidQuantityException.class)
    public ResponseEntity<?> handleInvalidQuantity(InvalidQuantityException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("code","BAD_REQUEST","message",ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("code","BAD_REQUEST","message","invalid quantity"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArg(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("code","BAD_REQUEST","message",ex.getMessage()));
    }
}

