package com.example.catalog.application.product;

import com.example.catalog.domain.common.PageResult;
import com.example.catalog.domain.product.Product;

import java.util.UUID;

public interface ProductCommands {
    Product create(String name, String shortDesc, UUID brandId, UUID categoryId, Boolean published);
    Product update(UUID id, String name, String shortDesc, UUID brandId, UUID categoryId, Boolean published);
    void delete(UUID id);
}

