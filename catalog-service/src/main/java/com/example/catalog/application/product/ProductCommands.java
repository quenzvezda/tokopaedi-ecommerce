package com.example.catalog.application.product;

import com.example.catalog.domain.product.Product;

import java.util.UUID;

public interface ProductCommands {
    Product create(UUID creatorId, String name, String shortDesc, UUID brandId, UUID categoryId, Boolean published);
    Product update(UUID actorId, UUID id, String name, String shortDesc, UUID brandId, UUID categoryId, Boolean published, boolean overrideOwnership);
    void delete(UUID actorId, UUID id, boolean overrideOwnership);
}

