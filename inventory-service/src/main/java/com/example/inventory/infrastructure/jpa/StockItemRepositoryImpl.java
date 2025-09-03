package com.example.inventory.infrastructure.jpa;

import com.example.inventory.domain.stock.StockItem;
import com.example.inventory.domain.stock.StockItemRepository;
import com.example.inventory.infrastructure.jpa.mapper.JpaMapper;
import com.example.inventory.infrastructure.jpa.repository.JpaStockItemRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class StockItemRepositoryImpl implements StockItemRepository {

    private final JpaStockItemRepository jpa;

    @Override
    public StockItem save(StockItem item) {
        return JpaMapper.toDomain(jpa.save(JpaMapper.toJpa(item)));
    }

    @Override
    public Optional<StockItem> findBySkuId(UUID skuId) {
        return jpa.findById(skuId).map(JpaMapper::toDomain);
    }

    @Override
    public List<StockItem> findByProductId(UUID productId) {
        return jpa.findByProductId(productId).stream().map(JpaMapper::toDomain).toList();
    }
}

