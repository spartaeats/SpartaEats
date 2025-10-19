package com.sparta.sparta_eats.item.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "p_item_option")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemOption {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@JdbcTypeCode(Types.VARCHAR)
	@Column(name = "id", columnDefinition = "char(36)", nullable = false, updatable = false)
	private UUID id;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "option_type", nullable = false)
	private Integer optionType;

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

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "item_id", nullable = false, columnDefinition = "char(36)")
	private Item item;
}