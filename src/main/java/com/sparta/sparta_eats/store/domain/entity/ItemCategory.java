package com.sparta.sparta_eats.store.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_item_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCategory {
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "category_id", nullable = false, length = 10)
    private String categoryId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;
}