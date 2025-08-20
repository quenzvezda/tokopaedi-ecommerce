package com.example.catalog.domain.sku;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkuRepository {
    Sku save(Sku sku);
    Optional<Sku> findById(UUID id);
    void deleteById(UUID id);
    List<Sku> findByProductId(UUID productId);
}
