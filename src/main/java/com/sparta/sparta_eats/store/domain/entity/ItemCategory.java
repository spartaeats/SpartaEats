package com.sparta.sparta_eats.store.domain.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private String id;

    @Column(name = "category_id", nullable = false, length = 10)
    private String categoryId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;
}