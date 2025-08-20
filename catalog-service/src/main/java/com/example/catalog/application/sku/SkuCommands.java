package com.example.catalog.application.sku;

import com.example.catalog.domain.sku.Sku;

import java.util.List;
import java.util.UUID;

public interface SkuCommands {
    Sku create(UUID productId, String skuCode, Boolean active, String barcode);
    Sku update(UUID id, String skuCode, Boolean active, String barcode);
    void delete(UUID id);
}

