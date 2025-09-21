package com.example.catalog.application.sku;

import com.example.catalog.domain.sku.Sku;

import java.util.UUID;

public interface SkuCommands {
    Sku create(UUID actorId, UUID productId, String skuCode, Boolean active, String barcode, boolean overrideOwnership);
    Sku update(UUID actorId, UUID id, String skuCode, Boolean active, String barcode, boolean overrideOwnership);
    void delete(UUID actorId, UUID id, boolean overrideOwnership);
}

