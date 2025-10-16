package com.sparta.sparta_eats.store.domain.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private String id;

    @Column(name = "cate_01", nullable = false, length = 10)
    private String cate01;

    @Column(name = "name", nullable = false, length = 100)
    private String name;
}