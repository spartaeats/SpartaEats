package com.sparta.sparta_eats.store.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "cate_01", nullable = false, length = 10)
    private String cate01;

    @Column(name = "name", nullable = false, length = 100)
    private String name;
}