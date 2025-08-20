package com.example.catalog.web.dto;

import jakarta.validation.constraints.NotBlank;

public record BrandUpdateRequest(
        @NotBlank String name,
        Boolean active
) {}
