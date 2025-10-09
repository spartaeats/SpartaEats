package com.sparta.sparta_eats.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "p_item_option")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemOption {
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private String id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "option_type", nullable = false)
    private Integer optionType; // 0: 단일 선택, 1: 다중 선택

    @Column(name = "add_price", nullable = false)
    private BigInteger addPrice;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
}