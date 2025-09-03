package com.example.inventory.web.mapper;

import com.example.inventory.domain.stock.StockItem;
import com.example.inventory.web.dto.StockItemResponse;

public final class DtoMapper {
    private DtoMapper() {}

    public static StockItemResponse toDto(StockItem s) {
        return new StockItemResponse(
                s.getSkuId(),
                s.getProductId(),
                s.getQtyOnHand(),
                s.getReserved(),
                s.isSellable()
        );
    }
}

