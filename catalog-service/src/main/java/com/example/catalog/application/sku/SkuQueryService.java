package com.example.catalog.application.sku;

import com.example.catalog.domain.sku.Sku;
import com.example.catalog.domain.sku.SkuRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class SkuQueryService implements SkuQueries {
    private final SkuRepository repo;

    @Override
    public List<Sku> byProduct(UUID productId) {
        return repo.findByProductId(productId);
    }
}
