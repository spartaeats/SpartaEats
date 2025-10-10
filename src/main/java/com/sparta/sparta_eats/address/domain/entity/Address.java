package com.sparta.sparta_eats.address.domain.entity;

import com.sparta.sparta_eats.address.domain.LocationInfo;
import com.sparta.sparta_eats.address.presentation.dto.request.AddressUpdateRequestV1;
import com.sparta.sparta_eats.address.presentation.dto.response.AddressDeleteResponseV1;
import com.sparta.sparta_eats.address.presentation.dto.response.AddressResponseV1;
import com.sparta.sparta_eats.global.entity.BaseEntity;
import com.sparta.sparta_eats.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_address")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(length = 20, nullable = false)
    private String name;
    @Column(nullable = false)
    private String addrRoad;
    @Column(nullable = false)
    private String addrDetail;
    @Embedded
    @Setter
    private Coordinate coordinate;
    @Column(nullable = false)
    @Setter
    @Getter
    private Boolean isDefault;
    @Column(length = 50)
    private String memo;
    @Column(length = 30)
    private String direction;
    @Column(length = 20)
    private String entrancePassword;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime deletedAt;
    @Column(length = 20)
    private String deletedBy;

    @Builder
    public Address(UUID id, String name, String addrRoad, String addrDetail, Coordinate coordinate, Boolean isDefault, String memo, String direction, String entrancePassword, LocalDateTime deletedAt, String deletedBy) {
        this.id = id;
        this.name = name;
        this.addrRoad = addrRoad;
        this.addrDetail = addrDetail;
        this.coordinate = coordinate;
        this.isDefault = isDefault;
        this.memo = memo;
        this.direction = direction;
        this.entrancePassword = entrancePassword;
        this.deletedAt = deletedAt;
        this.deletedBy = deletedBy;
    }

    public AddressResponseV1 toDto() {
        return AddressResponseV1.builder()
                .id(id)
                .name(name)
                .addrRoad(addrRoad)
                .addrDetail(addrDetail)
                .direction(direction)
                .entrancePassword(entrancePassword)
                .memo(memo)
                .isDefault(isDefault)
                .build();
    }

    public AddressDeleteResponseV1 toDeleteDto() {
        return AddressDeleteResponseV1
                .builder()
                .name(name)
                .addRoad(addrRoad)
                .deletedBy(deletedBy)
                .deletedAt(deletedAt)
                .build();
    }

    public void update(AddressUpdateRequestV1 updateRequest) {
        name = updateRequest.name();
        addrRoad = updateRequest.addrRoad();
        addrDetail = updateRequest.addrDetail();
        memo = updateRequest.memo();
        direction = updateRequest.direction();
        entrancePassword = updateRequest.entrancePassword();
    }

    public void delete(String userId) {
        deletedAt = LocalDateTime.now();
        deletedBy = userId;
    }

    public void assignUser(User user) {
        this.user = user;
    }

    public LocationInfo extractLocationInfo() {
        return LocationInfo.builder()
                .name(name)
                .address(addrDetail)
                .coordinate(coordinate)
                .build();
    }
}
