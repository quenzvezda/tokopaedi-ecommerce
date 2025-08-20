package com.example.catalog.web.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CategoryUpdateRequest(
        @NotBlank String name,
        UUID parentId,
        Boolean active,
        Integer sortOrder
) {}
