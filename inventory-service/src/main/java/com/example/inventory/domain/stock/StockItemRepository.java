package com.example.inventory.domain.stock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockItemRepository {
    StockItem save(StockItem item);
    Optional<StockItem> findBySkuId(UUID skuId);
    List<StockItem> findByProductId(UUID productId);
}

