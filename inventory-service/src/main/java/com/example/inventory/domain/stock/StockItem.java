package com.example.inventory.domain.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockItem {
    private UUID skuId;
    private UUID productId;
    private int qtyOnHand;
    private int reserved;
    private boolean sellable;
    private Instant createdAt;
    private Instant updatedAt;
}

