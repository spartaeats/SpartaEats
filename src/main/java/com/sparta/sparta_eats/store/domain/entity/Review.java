package com.sparta.sparta_eats.store.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import com.sparta.sparta_eats.item.domain.entity.Item;

@Entity
@Table(name = "p_review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
	@Id
	@Column(name = "id", columnDefinition = "UUID")
	private String id;

	@Column(name = "content", columnDefinition = "VARCHAR(300)")
	private String content;

	@Column(name = "rate", nullable = false)
	private Integer rate;

	@Column(name = "username", nullable = false, length = 100)
	private String username;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReviewImage> images;

	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Reply> replies;
}