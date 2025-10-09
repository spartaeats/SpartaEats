package com.sparta.sparta_eats.store.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "p_review_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewImage {
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private String id;

    @Column(name = "image", nullable = false, length = 255)
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;
}
