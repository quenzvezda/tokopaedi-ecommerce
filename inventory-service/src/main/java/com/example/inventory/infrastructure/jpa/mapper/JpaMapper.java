package com.example.inventory.infrastructure.jpa.mapper;

import com.example.inventory.domain.stock.ProcessedEvent;
import com.example.inventory.domain.stock.StockItem;
import com.example.inventory.infrastructure.jpa.entity.JpaProcessedEvent;
import com.example.inventory.infrastructure.jpa.entity.JpaStockItem;

public final class JpaMapper {
    private JpaMapper() {}

    public static JpaStockItem toJpa(StockItem s) {
        if (s == null) return null;
        return JpaStockItem.builder()
                .skuId(s.getSkuId())
                .productId(s.getProductId())
                .qtyOnHand(s.getQtyOnHand())
                .reserved(s.getReserved())
                .sellable(s.isSellable())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }

    public static StockItem toDomain(JpaStockItem j) {
        if (j == null) return null;
        return StockItem.builder()
                .skuId(j.getSkuId())
                .productId(j.getProductId())
                .qtyOnHand(j.getQtyOnHand())
                .reserved(j.getReserved())
                .sellable(j.isSellable())
                .createdAt(j.getCreatedAt())
                .updatedAt(j.getUpdatedAt())
                .build();
    }

    public static JpaProcessedEvent toJpa(ProcessedEvent e) {
        if (e == null) return null;
        return JpaProcessedEvent.builder()
                .eventId(e.getEventId())
                .eventType(e.getEventType())
                .processedAt(e.getProcessedAt())
                .build();
    }
}

