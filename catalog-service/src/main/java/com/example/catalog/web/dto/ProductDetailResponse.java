package com.example.catalog.web.dto;

import java.time.Instant;
import java.util.UUID;

public record ProductDetailResponse(
        UUID id, String name, String slug, String shortDesc,
        UUID brandId, UUID categoryId, boolean published, Instant createdAt, Instant updatedAt
) {}
