package com.example.inventory.application.stock;

import java.util.UUID;

public interface StockCommands {
    void handleSkuCreated(UUID eventId, UUID skuId, UUID productId);
    void handleSkuActivated(UUID eventId, UUID skuId);
    void handleSkuDeactivated(UUID eventId, UUID skuId);
}

