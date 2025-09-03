package com.example.inventory.application.stock;

import com.example.inventory.domain.stock.StockItem;
import com.example.inventory.domain.stock.StockItemRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class StockQueryService implements StockQueries {

    private final StockItemRepository repo;

    @Override
    public StockItem getBySkuId(UUID skuId) {
        return repo.findBySkuId(skuId).orElseThrow();
    }

    @Override
    public List<StockItem> getByProductId(UUID productId) {
        return repo.findByProductId(productId);
    }
}

