package com.sparta.sparta_eats.address.presentation.controller;

import com.sparta.sparta_eats.address.application.service.AddressServiceV1;
import com.sparta.sparta_eats.address.presentation.dto.request.AddressRequestV1;
import com.sparta.sparta_eats.address.presentation.dto.request.AddressUpdateRequestV1;
import com.sparta.sparta_eats.address.presentation.dto.response.AddressDeleteResponseV1;
import com.sparta.sparta_eats.address.presentation.dto.response.AddressResponseV1;
import com.sparta.sparta_eats.address.presentation.dto.response.DistanceResponse;
import com.sparta.sparta_eats.global.infrastructure.config.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@Tag(name = "주소 API", description = "주소 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/addresses")
public class AddressControllerV1 {
    private final AddressServiceV1 addressService;

    @Operation(summary = "주소 저장", description = "새로운 주소 저장 최대 20개까지 저장 가능")
    @PostMapping
    public ResponseEntity<AddressResponseV1> saveAddress(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                         @RequestBody AddressRequestV1 request) throws URISyntaxException {
        return  ResponseEntity.created(new URI("/"))
                .body(addressService.saveAddress(userDetails.getUser(), request));
    }

    @Operation(summary = "전체 주소 조회", description = "저장된 주소 전체 조회")
    @GetMapping
    public ResponseEntity<List<AddressResponseV1>> retrieveAddresses(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(addressService.getAddressList(userDetails.getUser()));
    }

    @Operation(summary = "주소 수정", description = "특정 주소 전체값 수정")
    @PutMapping
    public ResponseEntity<AddressResponseV1> updateAddress(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody AddressUpdateRequestV1 updateRequest) throws URISyntaxException {
        return ResponseEntity.ok(addressService.updateAddress(userDetails.getUser(), updateRequest));
    }

    @Operation(summary = "주소 기본값 지정", description = "기본 주소 변경")
    @PatchMapping("{id}/default")
    public ResponseEntity<AddressResponseV1> setAsDefault(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID id) {
        return ResponseEntity.ok(addressService.setAsDefault(userDetails.getUser(), id));
    }

    @Operation(summary = "주소 삭제", description = "주소 삭제, 실제 삭제는 이뤄지지 않고 soft delete 수행")
    @PatchMapping("{id}/delete")
    public ResponseEntity<AddressDeleteResponseV1> deleteAddress(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID id) {
        return ResponseEntity.ok(addressService.deleteAddress(userDetails.getUsername(), id));
    }

    @Operation(summary = "배달 거리 정보 조회", description = "특정 매장을 대상으로 현재 주소와의 거리, 배달 시간, 배달 요금 조회")
    @GetMapping("{addressId}/distance")
    public ResponseEntity<DistanceResponse> getDistanceInfo(@PathVariable UUID addressId, @RequestParam UUID storeId) {
        return ResponseEntity.ok(addressService.getDistanceInfo(addressId, storeId));
    }
}
