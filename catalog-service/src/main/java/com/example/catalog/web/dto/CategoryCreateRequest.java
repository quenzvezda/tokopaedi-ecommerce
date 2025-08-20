package com.example.catalog.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CategoryCreateRequest(
        @NotBlank String name,
        UUID parentId,
        Boolean active,
        Integer sortOrder
) {}

