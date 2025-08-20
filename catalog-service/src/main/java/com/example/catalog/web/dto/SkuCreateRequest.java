package com.example.catalog.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SkuCreateRequest(
        @NotNull UUID productId,
        @NotBlank String skuCode,
        Boolean active,
        String barcode
) {}
