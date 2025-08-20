package com.example.catalog.infrastructure.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "skus")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JpaSku {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "product_id", columnDefinition = "uuid", nullable = false)
    private UUID productId;

    @Column(name = "sku_code", nullable = false, unique = true, length = 120)
    private String skuCode;

    @Column(nullable = false)
    private boolean active;

    @Column(length = 120)
    private String barcode;
}
