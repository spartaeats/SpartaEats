package com.sparta.sparta_eats.order.application.service;

import com.sparta.sparta_eats.address.domain.dto.AddressSupplyDto;
import com.sparta.sparta_eats.address.domain.entity.Address;
import com.sparta.sparta_eats.address.domain.entity.Coordinate;
import com.sparta.sparta_eats.address.domain.repository.AddressRepository;
import com.sparta.sparta_eats.address.infrastructure.api.TmapApiClient;
import com.sparta.sparta_eats.global.domain.exception.NotFoundException;
import com.sparta.sparta_eats.order.domain.dto.OrderSnapshotDto;
import com.sparta.sparta_eats.order.domain.entity.Order;
import com.sparta.sparta_eats.order.domain.entity.OrderItem;
import com.sparta.sparta_eats.order.domain.entity.OrderItemOption;
import com.sparta.sparta_eats.order.domain.repository.OrderItemOptionRepository;
import com.sparta.sparta_eats.order.domain.repository.OrderItemRepository;
import com.sparta.sparta_eats.order.domain.repository.OrderRepository;
import com.sparta.sparta_eats.order.presentation.dto.request.OrderCreateRequest;
import com.sparta.sparta_eats.order.presentation.dto.response.OrderResponse;
import com.sparta.sparta_eats.store.entity.Item;
import com.sparta.sparta_eats.store.entity.ItemOption;
import com.sparta.sparta_eats.store.entity.Store;
import com.sparta.sparta_eats.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemOptionRepository orderItemOptionRepository;
    private final AddressRepository addressRepository;
    private final TmapApiClient tmapApiClient;

    private Order toOrderEntity(User user, OrderCreateRequest request) {
        // TODO StoreRepository에서 실제 Store 정보 불러와야함
        // 현재는 임시 Mock 객체
        Store store = new Store();
        return Order.builder()
                .user(user)
                .store(store)
                // TODO request에 Fulfillment 포함 필요
                .fulfillmentType(Order.FulfillmentType.PICKUP)
                .contactPhone(request.contactPhone())
                .memoToOwner(request.memoToOwner())
                .memoToRider(request.memoToRider())
                .noCutlery(request.noCutlery())
                .noSideDish(request.noSideDishes())
                .build();
    }

    private Map<UUID, Item> loadItemMap(OrderCreateRequest request) {
        List<UUID> itemIdList = request.items().stream()
                .map(OrderCreateRequest.OrderItemRequest::id)
                .toList();

        return null;
    }

    private Map<UUID, ItemOption> loadItemOptionMap(OrderCreateRequest request) {
        List<UUID> itemOptionIdList = request.items().stream()
                .flatMap(item -> Optional.ofNullable(item.options())
                        .orElse(Collections.emptyList()).stream())
                .map(OrderCreateRequest.OrderItemRequest.OrderItemOptionRequest::optionId)
                .toList();

        if (itemOptionIdList.isEmpty())
            return Collections.emptyMap();

        return null;
    }

    private long calculateItemTotal(OrderCreateRequest request, Map<UUID, Item> itemMap, Map<UUID, ItemOption> optionMap) {
        return request.items().stream()
                .mapToLong(itemReq -> {
                    Item item = itemMap.get(itemReq.id());
                    long itemPrice = item.getPrice().longValue();

                    long optionsPrice = Optional.ofNullable(itemReq.options()).orElse(Collections.emptyList()).stream()
                            .mapToLong(optReq -> optionMap.get(optReq.optionId()).getAddPrice().longValue())
                            .sum();

                    return (itemPrice + optionsPrice) * itemReq.quantity();
                })
                .sum();
    }

    private long calculateDeliveryFee(AddressSupplyDto supplyDto, Store store) {
        // TODO: Store의 Coordinate가 @Embedded 타입으로 변경되면 store.getCoordinate() 사용
        Coordinate storeCoordinate = Coordinate.builder()
                .addrLat(store.getLatitude())
                .addrLng(store.getLongitude())
                .build();

        return tmapApiClient.getDistance(supplyDto.coordinate(), storeCoordinate) * 100L;
    }


    @Transactional
    public OrderResponse createOrder(User user, OrderCreateRequest request) {
        Order newOrder = toOrderEntity(user, request);
        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 주소입니다."));
        newOrder.assignAddress(address.toSupplyDto());
        List<Item> items = List.of(Item.builder().price(BigInteger.valueOf(1000L)).build());
        BigInteger itemTotal = items.stream()
                .map(Item::getPrice)
                .reduce(BigInteger.ZERO, BigInteger::add);

        // TODO Store 객체의 longitude, latitude를 @Embedded Coordinate로 변경해야함.
        // TODO StoreRepository에서 store 정보를 불러와야함
        Store store = Store.builder().longitude(BigDecimal.ONE).latitude(BigDecimal.ONE).build();
        Coordinate storeCoordinate = Coordinate.builder()
                .addrLat(store.getLatitude())
                .addrLng(store.getLongitude())
                .build();

        BigInteger deliveryFee = BigInteger.valueOf(tmapApiClient.getDistance(address.toSupplyDto().coordinate(), storeCoordinate) * 100L);

        OrderSnapshotDto orderSnapshotDto = OrderSnapshotDto.builder()
                .itemTotal(itemTotal)
                .deliveryFee(deliveryFee)
                // Store에 할인정보가 없으므로 임시로 데이터를 둠
                .discountTotal(BigInteger.ZERO)
                .totalAmount(itemTotal.add(deliveryFee))
                .build();

        newOrder.assignItemSnapshot(orderSnapshotDto);
        orderRepository.save(newOrder);

        return  null;
    }
}
