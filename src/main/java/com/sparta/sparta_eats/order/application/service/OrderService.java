package com.sparta.sparta_eats.order.application.service;

import com.sparta.sparta_eats.address.domain.dto.AddressSupplyDto;
import com.sparta.sparta_eats.address.domain.entity.Address;
import com.sparta.sparta_eats.address.domain.entity.Coordinate;
import com.sparta.sparta_eats.address.domain.repository.AddressRepository;
import com.sparta.sparta_eats.address.infrastructure.api.TmapApiClient;
import com.sparta.sparta_eats.global.domain.exception.NotFoundException;
import com.sparta.sparta_eats.item.domain.entity.Item;
import com.sparta.sparta_eats.item.domain.repository.ItemOptionRepository;
import com.sparta.sparta_eats.item.domain.repository.ItemRepository;
import com.sparta.sparta_eats.order.domain.dto.OrderSnapshotDto;
import com.sparta.sparta_eats.order.domain.entity.Order;
import com.sparta.sparta_eats.order.domain.entity.OrderItem;
import com.sparta.sparta_eats.order.domain.entity.OrderItemOption;
import com.sparta.sparta_eats.order.domain.repository.OrderItemOptionRepository;
import com.sparta.sparta_eats.order.domain.repository.OrderItemRepository;
import com.sparta.sparta_eats.order.domain.repository.OrderRepository;
import com.sparta.sparta_eats.order.presentation.dto.request.OrderCreateRequest;
import com.sparta.sparta_eats.order.presentation.dto.request.OrderSearchCondition;
import com.sparta.sparta_eats.order.presentation.dto.response.OrderCreateResponse;
import com.sparta.sparta_eats.order.presentation.dto.response.OrderListResponse;
import com.sparta.sparta_eats.order.presentation.dto.response.OrderSingleResponse;
import com.sparta.sparta_eats.store.domain.entity.ItemOption;
import com.sparta.sparta_eats.store.domain.entity.Store;
import com.sparta.sparta_eats.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemOptionRepository orderItemOptionRepository;
    private final ItemRepository itemRepository;
    private final ItemOptionRepository itemOptionRepository;
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

        List<Item> items = itemRepository.findAllById(itemIdList);

        if (items.size() != itemIdList.size()) {
            throw new IllegalArgumentException("존재하지 않거나 유효하지 않은 상품 ID가 포함되어 있습니다.");
        }

        return items.stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
    }

    private Map<UUID, ItemOption> fetchItemOptionMap(OrderCreateRequest request) {
        List<UUID> itemOptionIdList = request.items().stream()
                .flatMap(item -> Optional.ofNullable(item.options())
                        .orElse(Collections.emptyList()).stream())
                .map(OrderCreateRequest.OrderItemRequest.OrderItemOptionRequest::optionId)
                .toList();
        if (itemOptionIdList.isEmpty()) {
            return Collections.emptyMap();
        }

        List<ItemOption> options = itemOptionRepository.findAllById(itemOptionIdList);
        if (options.size() != itemOptionIdList.size()) {
            throw new IllegalArgumentException("존재하지 않거나 유효하지 않은 옵션 ID가 포함되어 있습니다.");
        }

        return options.stream()
                .collect(Collectors.toMap(ItemOption::getId, Function.identity()));
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

    private List<OrderCreateResponse.ItemResponse> buildAndSaveOrderItem(Order newOrder, OrderCreateRequest request, Map<UUID, Item> itemMap, Map<UUID, ItemOption> itemOptionMap) {
        List<OrderItemOption> orderItemOptionList = new ArrayList<>();
        List<OrderItem> orderItemList = new ArrayList<>();
        request.items()
                .forEach(itemRequest -> {
                    Item item = itemMap.get(itemRequest.id());
                    OrderItem orderItem = OrderItem.builder()
                            .order(newOrder)
                            .item(item)
                            .itemName(item.getName())
                            .thumbnailUrl(item.getImage())
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
        List<OrderItemOption> savedOrderItemOptions = orderItemOptionRepository.saveAll(orderItemOptionList);

        Map<UUID, List<OrderItemOption>> optionsMapByItemId = savedOrderItemOptions.stream()
                .collect(Collectors.groupingBy(
                        // Key: 부모 OrderItem의 ID를 가져옴
                        orderItemOption -> orderItemOption.getOrderItem().getId()
                ));

        return savedOrderItems.stream()
                .map(orderItem -> {
                    // Map에서 현재 OrderItem의 ID에 해당하는 옵션 리스트를 가져옴
                    // getOrDefault를 사용하면 옵션이 없는 경우에도 안전하게 빈 리스트를 반환
                    List<OrderItemOption> currentOptions = optionsMapByItemId.getOrDefault(orderItem.getId(), Collections.emptyList());

                    // 3. 가져온 옵션 엔티티 리스트를 OptionResponse DTO 리스트로 변환
                    List<OrderCreateResponse.OptionResponse> optionResponses = currentOptions.stream()
                            .map(option -> OrderCreateResponse.OptionResponse.builder()
                                    .id(option.getItemOptionId())
                                    .name(option.getOptionName())
                                    .price(option.getAddPrice())
                                    .build())
                            .toList();

                    // 4. 최종 ItemResponse DTO 생성
                    return OrderCreateResponse.ItemResponse.builder()
                            .id(orderItem.getOrder().getStore().getId())
                            .name(orderItem.getItemName())
                            .quantity(orderItem.getQuantity())
                            .unitPrice(orderItem.getUnitPrice())
                            .linePrice(orderItem.getLinePrice())
                            .options(optionResponses)
                            .build();
                })
                .toList();
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
    public OrderCreateResponse createOrder(User user, OrderCreateRequest request) {
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

        Order savedOrder = orderRepository.save(newOrder);
        List<OrderCreateResponse.ItemResponse> itemResponses = buildAndSaveOrderItem(newOrder, request, itemMap, itemOptionMap);


        return OrderCreateResponse.builder()
                .id(savedOrder.getId())
                .status(savedOrder.getStatus())
                .createdAt(savedOrder.getCreatedAt())
                .store(OrderCreateResponse.StoreResponse.builder()
                        .id(store.getId())
                        .name(store.getName()).build())
                .items(itemResponses)
                .amounts(OrderCreateResponse.Amounts.builder()
                        .currency(Currency.getInstance("KRW"))
                        .vatIncluded(true)
                        .itemsTotal(snapshotDto.itemTotal())
                        .deliveryFee(snapshotDto.deliveryFee())
                        .discountTotal(snapshotDto.discountTotal())
                        .payableTotal(snapshotDto.totalAmount())
                        .build())
                .delivery(OrderCreateResponse.Delivery.builder()
                        .addressSummary(addressSupplyDto.addrDetail())
                        .build())
                .contactPhone(savedOrder.getContactPhone())
                .flags(OrderCreateResponse.Flags.builder()
                        .noCutlery(newOrder.getNoCutlery())
                        .noSideDishes(newOrder.getNoSideDish())
                        .build())
                .build();
    }

    public Page<OrderListResponse> searchOrders(OrderSearchCondition condition, Pageable pageable) {

        Page<Order> orderPage = orderRepository.search(condition, pageable);
        List<Order> orderList = orderPage.getContent();

        if (orderList.isEmpty()) {
            return Page.empty(pageable);
        }

        List<OrderItem> orderItems = orderItemRepository.findAllByOrderIn(orderList);

        // 3. 조회된 OrderItem 목록으로 관련된 모든 OrderItemOption들을 한 번에 조회 (쿼리 1번)
        List<OrderItemOption> orderItemOptions = orderItemOptionRepository.findAllByOrderItemIn(orderItems);

        // 4. DTO 조립을 위해 조회된 데이터를 Map으로 가공 (메모리 작업)
        Map<UUID, List<OrderItemOption>> optionsMap = orderItemOptions.stream()
                .collect(Collectors.groupingBy(option -> option.getOrderItem().getId()));

        Map<UUID, List<OrderItem>> itemsMap = orderItems.stream()
                .collect(Collectors.groupingBy(item -> item.getOrder().getId()));

        // 5. 최종 OrderResponse DTO 리스트 생성
        List<OrderListResponse> responseContent = orderList.stream()
                .map(order -> {
                    List<OrderItem> currentItems = itemsMap.getOrDefault(order.getId(), Collections.emptyList());

                    List<OrderListResponse.ItemResponse> itemResponses = currentItems.stream()
                            .map(item -> {
                                StringBuilder builder = new StringBuilder();
                                List<OrderItemOption> currentOptions = optionsMap.getOrDefault(item.getId(), Collections.emptyList());
                                currentOptions.forEach(option -> {
                                    builder.append(option.getOptionName());
                                    builder.append(", ");
                                });

                                return OrderListResponse.ItemResponse.builder()
                                        .name(item.getItemName())
                                        .quantity(item.getQuantity())
                                        .optionsText(builder.toString())
                                        .build();
                            }).toList();

                    return OrderListResponse.builder()
                            .id(order.getId())
                            .storeId(order.getStore().getId())
                            .storeName(order.getStore().getName())
                            .storeImage(order.getStore().getImage())
                            .items(itemResponses)
                            .totalAmount(order.getTotalAmount())
                            .status(order.getStatus())
                            .createdAt(order.getCreatedAt())
                            .pageable(pageable)
                            .totalElements(orderPage.getTotalElements())
                            .totalPages(orderPage.getTotalPages())
                            .hasNext(orderPage.hasNext())
                            .build();
                }).toList();

        return new PageImpl<>(responseContent, pageable, orderPage.getTotalElements());
    }

    public OrderSingleResponse getOrderDetail(User user, UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("id와 일치하는 주문이 존재하지 않습니다."));
        List<OrderItem> orderItemList = orderItemRepository.findAllByOrder(order);
        List<OrderItemOption> allOptions = orderItemOptionRepository.findAllByOrderItemIn(orderItemList);
        Store store = order.getStore();

        Map<UUID, List<OrderItemOption>> optionsMapByOrderItemId = allOptions.stream()
                .collect(Collectors.groupingBy(
                        // Key: 각 옵션의 부모인 OrderItem의 ID
                        option -> option.getOrderItem().getId()
                ));

        List<OrderSingleResponse.ItemResponse> itemResponses = orderItemList.stream()
                .map(orderItem -> {
                    // Map에서 현재 orderItem의 ID에 해당하는 옵션 리스트를 가져옴
                    List<OrderItemOption> currentOptions = optionsMapByOrderItemId.getOrDefault(orderItem.getId(), Collections.emptyList());

                    // 가져온 옵션 엔티티 리스트를 DTO 리스트로 변환
                    List<OrderSingleResponse.OptionResponse> options = currentOptions.stream()
                            .map(option -> OrderSingleResponse.OptionResponse.builder()
                                    .optionId(option.getItemOptionId())
                                    .name(option.getOptionName())
                                    .build())
                            .toList();

                    return OrderSingleResponse.ItemResponse.builder()
                            .id(orderItem.getItem().getId())
                            .name(orderItem.getItemName())
                            .basePrice(BigDecimal.valueOf(orderItem.getItem().getPrice().longValue()))
                            .optionsPrice(orderItem.getOptionTotal())
                            .unitPrice(orderItem.getUnitPrice())
                            .quantity(orderItem.getQuantity())
                            .calculatedLinePrice(orderItem.getLinePrice())
                            .options(options)
                            .build();
                })
                .toList();

        return OrderSingleResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .store(OrderSingleResponse.StoreResponse.builder()
                        .id(store.getId())
                        .name(store.getName())
                        .build())
                .items(itemResponses)
                .amounts(OrderSingleResponse.AmountsResponse.builder()
                        .itemsTotal(order.getItemTotal())
                        .deliveryFee(order.getDeliveryFee())
                        .discountTotal(order.getDiscountTotal())
                        .payableTotal(order.getDiscountTotal())
                        .build())
                .delivery(OrderSingleResponse.DeliveryResponse.builder()
                        .addressSummary(order.getAddrDetail())
                        .build())
                .flags(OrderSingleResponse.FlagsResponse.builder()
                        .noCutlery(order.getNoCutlery())
                        .noSideDishes(order.getNoSideDish())
                        .build())
                .build();
    }
}
