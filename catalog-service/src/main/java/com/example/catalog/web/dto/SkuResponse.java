package com.example.catalog.web.dto;

import java.util.UUID;

public record SkuResponse(
        UUID id, UUID productId, String skuCode, boolean active, String barcode
) {}
