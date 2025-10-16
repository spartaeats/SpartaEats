package com.sparta.sparta_eats.store.entity;

import com.sparta.sparta_eats.global.util.MoneyLongConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "p_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private String id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Digits(integer = 19, fraction = 0)
    @Convert(converter = MoneyLongConverter.class)
    @Column(name = "price", nullable = false) // DB: BIGINT
    private BigDecimal price;

    @Digits(integer = 19, fraction = 0)
    @Convert(converter = MoneyLongConverter.class)
    @Column(name = "sale_price")              // DB: BIGINT (nullable)
    private BigDecimal salePrice;

    @Column(name = "image", length = 255)
    private String image;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "sold_out", nullable = false)
    private Boolean soldOut;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ItemCategory itemCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
}