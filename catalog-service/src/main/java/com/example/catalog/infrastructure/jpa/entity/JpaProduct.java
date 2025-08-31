package com.example.catalog.infrastructure.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JpaProduct {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 220, unique = true)
    private String slug;

    @Column(name = "short_desc", length = 1000)
    private String shortDesc;

    @Column(name = "brand_id", columnDefinition = "uuid", nullable = false)
    private UUID brandId;

    @Column(name = "category_id", columnDefinition = "uuid", nullable = false)
    private UUID categoryId;

    @Column(nullable = false)
    private boolean published;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
