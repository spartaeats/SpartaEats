package com.sparta.sparta_eats.order.application.service;

import com.sparta.sparta_eats.address.domain.dto.AddressSupplyDto;
import com.sparta.sparta_eats.address.domain.entity.Address;
import com.sparta.sparta_eats.address.domain.entity.Coordinate;
import com.sparta.sparta_eats.address.domain.repository.AddressRepository;
import com.sparta.sparta_eats.address.infrastructure.api.TmapApiClient;
import com.sparta.sparta_eats.global.domain.exception.NotFoundException;
import com.sparta.sparta_eats.item.domain.entity.Item;
import com.sparta.sparta_eats.order.domain.dto.OrderSnapshotDto;
import com.sparta.sparta_eats.order.domain.entity.Order;
import com.sparta.sparta_eats.order.domain.entity.OrderItem;
import com.sparta.sparta_eats.order.domain.entity.OrderItemOption;
import com.sparta.sparta_eats.order.domain.repository.OrderItemOptionRepository;
import com.sparta.sparta_eats.order.domain.repository.OrderItemRepository;
import com.sparta.sparta_eats.order.domain.repository.OrderRepository;
import com.sparta.sparta_eats.order.presentation.dto.request.OrderCreateRequest;
import com.sparta.sparta_eats.order.presentation.dto.response.OrderResponse;
import com.sparta.sparta_eats.store.entity.ItemOption;
import com.sparta.sparta_eats.store.entity.Store;
import com.sparta.sparta_eats.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemOptionRepository orderItemOptionRepository;
    private final AddressRepository addressRepository;
    private final TmapApiClient tmapApiClient;

    private Order toOrderEntity(User user, Store store, OrderCreateRequest request) {
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

    private Map<UUID, Item> fetchItemMap(OrderCreateRequest request) {
        List<UUID> itemIdList = request.items().stream()
                .map(OrderCreateRequest.OrderItemRequest::id)
                .toList();

        return null;
    }

    private Map<UUID, ItemOption> fetchItemOptionMap(OrderCreateRequest request) {
        List<UUID> itemOptionIdList = request.items().stream()
                .flatMap(item -> Optional.ofNullable(item.options())
                        .orElse(Collections.emptyList()).stream())
                .map(OrderCreateRequest.OrderItemRequest.OrderItemOptionRequest::optionId)
                .toList();

        if (itemOptionIdList.isEmpty())
            return Collections.emptyMap();

        return null;
    }

    private BigDecimal calculateItemTotal(OrderCreateRequest request, Map<UUID, Item> itemMap, Map<UUID, ItemOption> optionMap) {
        return BigDecimal.valueOf(request.items().stream()
                .mapToLong(itemReq -> {
                    Item item = itemMap.get(itemReq.id());
                    long itemPrice = item.getPrice().longValue();

                    long optionsPrice = Optional.ofNullable(itemReq.options()).orElse(Collections.emptyList()).stream()
                            .mapToLong(optReq -> optionMap.get(optReq.optionId()).getAddPrice().longValue())
                            .sum();

                    return (itemPrice + optionsPrice) * itemReq.quantity();
                })
                .sum());
    }

    private List<OrderItem> buildAndSaveOrderItem(Order newOrder, OrderCreateRequest request, Map<UUID, Item> itemMap, Map<UUID, ItemOption> itemOptionMap) {
        List<OrderItemOption> orderItemOptionList = new ArrayList<>();
        List<OrderItem> orderItemList = new ArrayList<>();
        request.items()
                .forEach(itemRequest -> {
                    Item item = itemMap.get(itemRequest.id());
                    OrderItem orderItem = OrderItem.builder()
                            .order(newOrder)
                            .item(item)
                            .itemName(item.getName())
                            // TODO Item에 thumbnailUrl 컬럼 만들어야함
                            .thumbnailUrl("temp")
                            .unitPrice(BigDecimal.valueOf(item.getPrice().longValue()))
                            .quantity(itemRequest.quantity())
                            .optionComboHash("")
                            .build();

                    Optional.ofNullable(itemRequest.options())
                            .orElse(Collections.emptyList())
                            .forEach(itemOptionRequest -> {
                                ItemOption itemOption = itemOptionMap.get(itemOptionRequest.optionId());

                                OrderItemOption orderItemOption = OrderItemOption.builder()
                                        .orderItem(orderItem)
                                        .itemOptionId(itemOptionRequest.optionId())
                                        .optionName(itemOption.getName())
                                        .addPrice(BigDecimal.valueOf(itemRequest.quantity()))
                                        .quantity(itemRequest.quantity())
                                        .build();

                                orderItemOptionList.add(orderItemOption);
                            });

                    orderItemList.add(orderItem);
                });

        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItemList);
        orderItemOptionRepository.saveAll(orderItemOptionList);

        return savedOrderItems;
    }

    private OrderSnapshotDto fetchOrderSnapshotDto(AddressSupplyDto addressSupplyDto, Store store, Map<UUID, Item> itemMap, Map<UUID, ItemOption> itemOptionMap, OrderCreateRequest request) {
        BigDecimal itemTotal = calculateItemTotal(request, itemMap, itemOptionMap);
        Coordinate storeCoordinate = Coordinate.builder()
                .addrLat(store.getLatitude())
                .addrLng(store.getLongitude())
                .build();

        BigDecimal deliveryFee = BigDecimal.valueOf(tmapApiClient.getDistance(addressSupplyDto.coordinate(), storeCoordinate) * 100L);

        return OrderSnapshotDto.builder()
                .itemTotal(itemTotal)
                .deliveryFee(deliveryFee)
                // TODO Store에 할인정보 추가 or 할인정보 배제
                // Store에 할인정보가 없으므로 임시로 데이터를 둠
                .discountTotal(BigDecimal.ZERO)
                .totalAmount(itemTotal.add(deliveryFee))
                .build();
    }


    @Transactional
    public OrderResponse createOrder(User user, OrderCreateRequest request) {
        Map<UUID, Item> itemMap = fetchItemMap(request);
        Map<UUID, ItemOption> itemOptionMap = fetchItemOptionMap(request);
        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 주소입니다."));
        // TODO Store 객체의 longitude, latitude를 @Embedded Coordinate로 변경해야함.
        // TODO StoreRepository에서 store 정보를 불러와야함
        // 현재는 임시 객체
        Store store = Store.builder().longitude(BigDecimal.ONE).latitude(BigDecimal.ONE).build();
        AddressSupplyDto addressSupplyDto = address.toSupplyDto();

        Order newOrder = toOrderEntity(user, store, request);
        newOrder.assignAddress(addressSupplyDto);

        OrderSnapshotDto snapshotDto = fetchOrderSnapshotDto(addressSupplyDto, store, itemMap, itemOptionMap, request);
        newOrder.assignItemSnapshot(snapshotDto);

        orderRepository.save(newOrder);
        buildAndSaveOrderItem(newOrder, request, itemMap, itemOptionMap);

        return null;
    }
}
