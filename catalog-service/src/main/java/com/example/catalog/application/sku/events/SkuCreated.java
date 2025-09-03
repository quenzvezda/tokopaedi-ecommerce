package com.example.catalog.application.sku.events;

import java.util.UUID;

public record SkuCreated(UUID skuId, UUID productId, boolean active) {}

