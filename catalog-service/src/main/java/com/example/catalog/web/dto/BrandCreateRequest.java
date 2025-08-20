package com.example.catalog.web.dto;

import jakarta.validation.constraints.NotBlank;

public record BrandCreateRequest(
        @NotBlank String name,
        Boolean active
) {}
