package com.example.catalog.web.dto;

import java.util.UUID;

public record CategoryResponse(
        UUID id, UUID parentId, String name, boolean active, Integer sortOrder
) {}

