package com.example.catalog.web.dto;

public record SkuUpdateRequest(
        String skuCode,
        Boolean active,
        String barcode
) {}
