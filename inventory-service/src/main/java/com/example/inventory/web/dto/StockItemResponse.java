package com.example.inventory.web.dto;

import java.util.UUID;

public record StockItemResponse(
        UUID skuId,
        UUID productId,
        int qtyOnHand,
        int reserved,
        boolean sellable
) {}

