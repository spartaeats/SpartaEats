package com.sparta.sparta_eats.address.presentation.controller;

import com.sparta.sparta_eats.address.application.service.AddressServiceV1;
import com.sparta.sparta_eats.address.presentation.dto.request.AddressRequestV1;
import com.sparta.sparta_eats.address.presentation.dto.request.AddressUpdateRequestV1;
import com.sparta.sparta_eats.address.presentation.dto.response.AddressDeleteResponseV1;
import com.sparta.sparta_eats.address.presentation.dto.response.AddressResponseV1;
import com.sparta.sparta_eats.global.infrastructure.config.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/addresses")
public class AddressControllerV1 {
    private AddressServiceV1 addressService;

    @PostMapping
    public ResponseEntity<AddressResponseV1> saveAddress(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                         @RequestBody AddressRequestV1 request) throws URISyntaxException {
        return addressService.saveAddress(userDetails.getUser(), request);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponseV1>> retrieveAddresses(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return addressService.getAddressList(userDetails.getUser());
    }

    @PutMapping
    public ResponseEntity<AddressResponseV1> updateAddress(@AuthenticationPrincipal UserDetailsImpl userDetails, AddressUpdateRequestV1 updateRequest) throws URISyntaxException {
        return addressService.updateAddress(userDetails.getUser(), updateRequest);
    }

    @PatchMapping
    @RequestMapping("/default")
    public ResponseEntity<AddressResponseV1> setAsDefault(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UUID id) {
        return addressService.setAsDefaultV1(userDetails.getUser(), id);
    }

    @PatchMapping
    @RequestMapping("/delete")
    public ResponseEntity<AddressDeleteResponseV1> deleteAddress(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UUID id) {
        return addressService.deleteAddress(userDetails.getUsername(), id);
    }
}
