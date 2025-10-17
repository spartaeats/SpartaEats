package com.sparta.sparta_eats.item.domain.entity;

import com.sparta.sparta_eats.store.domain.entity.ItemCategory;
import com.sparta.sparta_eats.store.domain.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

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
	private UUID id;

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "price", nullable = false)
	private BigInteger price;

	@Column(name = "sale_price")
	private BigInteger salePrice;

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