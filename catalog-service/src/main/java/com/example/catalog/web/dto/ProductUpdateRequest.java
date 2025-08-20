package com.example.catalog.web.dto;

import java.util.UUID;

public record ProductUpdateRequest(
        String name,
        String shortDesc,
        UUID brandId,
        UUID categoryId,
        Boolean published
) {}
