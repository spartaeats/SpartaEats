package com.sparta.sparta_eats.address.application.service;

import com.sparta.sparta_eats.address.domain.LocationInfo;
import com.sparta.sparta_eats.address.domain.entity.Address;
import com.sparta.sparta_eats.address.domain.entity.Coordinate;
import com.sparta.sparta_eats.address.domain.repository.AddressRepository;
import com.sparta.sparta_eats.address.infrastructure.api.KakaoApiClient;
import com.sparta.sparta_eats.address.infrastructure.api.TmapApiClient;
import com.sparta.sparta_eats.address.presentation.dto.request.AddressRequestV1;
import com.sparta.sparta_eats.address.presentation.dto.request.AddressUpdateRequestV1;
import com.sparta.sparta_eats.address.presentation.dto.response.AddressDeleteResponseV1;
import com.sparta.sparta_eats.address.presentation.dto.response.AddressResponseV1;
import com.sparta.sparta_eats.address.presentation.dto.response.DistanceResponse;
import com.sparta.sparta_eats.global.domain.exception.NotFoundException;
import com.sparta.sparta_eats.store.domain.entity.Store;
import com.sparta.sparta_eats.store.domain.repository.StoreRepository;
import com.sparta.sparta_eats.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceV1 {
    private final AddressRepository addressRepository;
    private final KakaoApiClient kakaoApiClient;
    private final TmapApiClient tmapApiClient;
    private final StoreRepository storeRepository;

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
    public AddressResponseV1 saveAddress(User user, AddressRequestV1 request) throws URISyntaxException {
        addressRepository.findByUserAndIsDefault(user, true)
                .ifPresent(address -> address.setIsDefault(false));

        Address newAddress = toEntity(request);
        newAddress.setCoordinate(kakaoApiClient.loadCoordinate(request.addrRoad()));
        newAddress.setIsDefault(true);
        newAddress.assignUser(user);

        addressRepository.save(newAddress);

        return newAddress.toDto();
    }

    public List<AddressResponseV1> getAddressList(User user) {
        return addressRepository.findAllByUser(user)
                .stream().map(Address::toDto).toList();
    }

    @Transactional
    public AddressResponseV1 updateAddress(User user, AddressUpdateRequestV1 updateRequest) throws URISyntaxException {
        Address address = addressRepository.findById(updateRequest.id())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 주소입니다."));
        address.update(updateRequest);

        address.setCoordinate(kakaoApiClient.loadCoordinate(updateRequest.addrRoad()));

        return address.toDto();
    }

    @Transactional
    public AddressResponseV1 setAsDefault(User user, UUID id) {
        addressRepository.unsetAllDefaultsByUser(user);

        Address newDefault = addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 주소입니다."));
        newDefault.setIsDefault(true);

        return newDefault.toDto();
    }

    @Transactional
    public AddressDeleteResponseV1 deleteAddress(String userId, UUID id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 주소입니다."));

        address.delete(userId);

        return address.toDeleteDto();

    }

    public DistanceResponse getDistanceInfo(UUID addressId, UUID storeId) {
        LocationInfo start = addressRepository.findById(addressId).orElseThrow().extractLocationInfo();
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("아이디와 일치하는 매장이 존재하지 않습니다."));
        LocationInfo target = LocationInfo.builder()
                .address(store.getAddress())
                .coordinate(Coordinate.builder()
                        .addrLng(store.getLongitude())
                        .addrLat(store.getLatitude())
                        .build())
                .name("무봉리 토종순대국 광화문점")
                .build();
        // TODO store 연동 되면 store charge 정보 가져와야함
        // 이후에 요금 계산 정책이 복잡해 질 시에 계산 로직을 클래스로 분리
        int charge = 0;
        int distance = tmapApiClient.getDistance(start.getCoordinate(), target.getCoordinate());
        int time = tmapApiClient.getTime(start.getCoordinate(), target.getCoordinate());
        charge += (distance - 2000) / 1000 * 100;
        if(charge < 0)
            charge = 0;

        return DistanceResponse.builder()
                .start(start)
                .target(target)
                .charge(charge)
                .distance(distance)
                .time(time)
                .build();
    }

}