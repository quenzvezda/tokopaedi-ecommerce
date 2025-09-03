package com.example.inventory.application.stock;

import com.example.inventory.domain.stock.StockItem;

import java.util.List;
import java.util.UUID;

public interface StockQueries {
    StockItem getBySkuId(UUID skuId);
    List<StockItem> getByProductId(UUID productId);
}

