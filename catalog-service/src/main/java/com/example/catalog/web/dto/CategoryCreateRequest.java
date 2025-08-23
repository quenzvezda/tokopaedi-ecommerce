package com.example.catalog.web.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CategoryCreateRequest(
        @NotBlank String name,
        UUID parentId,
        Boolean active,
        Integer sortOrder
) {}

