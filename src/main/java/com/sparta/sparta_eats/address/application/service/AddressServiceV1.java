package com.sparta.sparta_eats.address.application.service;

import com.sparta.sparta_eats.address.domain.entity.Address;
import com.sparta.sparta_eats.address.domain.entity.Coordinate;
import com.sparta.sparta_eats.address.domain.repository.AddressRepository;
import com.sparta.sparta_eats.address.infrastructure.KakaoApiClient;
import com.sparta.sparta_eats.address.presentation.dto.request.AddressRequestV1;
import com.sparta.sparta_eats.address.presentation.dto.request.AddressUpdateRequestV1;
import com.sparta.sparta_eats.address.presentation.dto.response.AddressDeleteResponseV1;
import com.sparta.sparta_eats.address.presentation.dto.response.AddressResponseV1;
import com.sparta.sparta_eats.address.presentation.dto.response.KakaoCoordinateResponse;
import com.sparta.sparta_eats.global.domain.exception.BadRequestException;
import com.sparta.sparta_eats.global.domain.exception.NotFoundException;
import com.sparta.sparta_eats.user.domain.entity.User;
import com.sparta.sparta_eats.user.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressServiceV1 {
    private final AddressRepository addressRepository;
    private final KakaoApiClient kakaoApiClient;

    private Address toEntity(AddressRequestV1 request) {
        return Address.builder()
                .name(request.name())
                .addrRoad(request.addrRoad())
                .addrDetail(request.addrDetail())
                .memo(request.memo())
                .direction(request.direction())
                .entrancePassword(request.entrancePassword())
                .build();
    }

    @Transactional
    public ResponseEntity<AddressResponseV1> saveAddress(User user, AddressRequestV1 request) throws URISyntaxException {
        Address defaultAddress = addressRepository.findByUserAndIsDefault(user, true)
                .orElseThrow(() -> new NotFoundException("기본 주소가 없습니다."));
        defaultAddress.setIsDefault(false);

        Address newAddress = toEntity(request);
        newAddress.setCoordinate(kakaoApiClient.loadCoordinate(request.addrRoad()));
        newAddress.setIsDefault(true);

        return ResponseEntity.created(new URI("/"))
                .body(newAddress.toDto());
    }

    public ResponseEntity<List<AddressResponseV1>> getAddressList(User user) {
        return ResponseEntity.ok(addressRepository.findAllByUser(user)
                .stream().map(Address::toDto).toList());
    }

    @Transactional
    public ResponseEntity<AddressResponseV1> updateAddress(User user, AddressUpdateRequestV1 updateRequest) throws URISyntaxException {
        Address address = addressRepository.findById(updateRequest.id())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 주소입니다."));
        address.update(updateRequest);
        address.setCoordinate(kakaoApiClient.loadCoordinate(updateRequest.addrRoad()));

        return ResponseEntity.ok(address.toDto());
    }

    // QueryDSL 고려..
    @Transactional
    public ResponseEntity<AddressResponseV1> setAsDefaultV1(User user, UUID id) {
        Address defaultAddress = addressRepository.findByUserAndIsDefault(user, true)
                .orElseThrow(() -> new NotFoundException("기본 주소가 없습니다."));
        defaultAddress.setIsDefault(false);

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 주소입니다."));
        address.setIsDefault(true);

        return ResponseEntity.ok(address.toDto());
    }

    @Transactional
    public ResponseEntity<AddressDeleteResponseV1> deleteAddress(String userId, UUID id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 주소입니다."));

        address.delete(userId);

        return ResponseEntity.ok()
                .body(address.toDeleteDto());

    }

}
