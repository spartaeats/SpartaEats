package com.sparta.sparta_eats.cart.presentation.dto.response;


public record ResCreateCartResultV1(
        ResCartV1 cart,
        boolean created,
        boolean reused,
        boolean replacedPreviousStoreCart
) {}

