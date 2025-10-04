package com.sparta.sparta_eats.address.presentation.controller;

import com.sparta.sparta_eats.address.application.service.AddressServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/addresses")
public class AddressControllerV1 {
    private AddressServiceV1 addressService;
}
