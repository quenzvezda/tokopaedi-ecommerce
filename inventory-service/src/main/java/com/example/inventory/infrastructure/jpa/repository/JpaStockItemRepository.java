package com.example.inventory.infrastructure.jpa.repository;

import com.example.inventory.infrastructure.jpa.entity.JpaStockItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaStockItemRepository extends JpaRepository<JpaStockItem, UUID> {
    List<JpaStockItem> findByProductId(UUID productId);
}

