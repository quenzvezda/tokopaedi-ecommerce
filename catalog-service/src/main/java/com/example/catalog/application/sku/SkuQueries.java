package com.example.catalog.application.sku;

import com.example.catalog.domain.sku.Sku;

import java.util.List;
import java.util.UUID;

public interface SkuQueries {
    List<Sku> byProduct(UUID productId);
}
