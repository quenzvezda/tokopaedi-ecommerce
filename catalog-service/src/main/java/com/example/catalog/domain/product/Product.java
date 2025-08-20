package com.example.catalog.domain.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private UUID id;
    private String name;
    private String shortDesc; // nullable
    private UUID brandId;
    private UUID categoryId;
    private boolean published;
    private Instant createdAt;
    private Instant updatedAt;
}
