package com.example.catalog.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProductCreateRequest(
        @NotBlank String name,
        String shortDesc,
        @NotNull UUID brandId,
        @NotNull UUID categoryId,
        Boolean published
) {}
