package com.sparta.sparta_eats.store.domain.entity;

import com.sparta.sparta_eats.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "p_store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "addr_detail")
    private String addressDetail;

    @Column(name = "image")
    private String image;

    @Column(name = "phone", nullable = false, length = 13)
    private String phone;

    @Column(name = "open_hour")
    private LocalTime openHour;

    @Column(name = "close_hour")
    private LocalTime closeHour;

    @Column(name = "status_day", length = 10)
    private String statusDay;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false)
    private Boolean status; // 0: Close, 1: Open

    @Column(name = "add_lat", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "add_lng", precision = 10, scale = 7)
    private BigDecimal longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner; //유저 연결
}