package com.example.inventory.web;

import com.example.inventory.application.stock.StockQueries;
import com.example.inventory.web.dto.StockItemResponse;
import com.example.inventory.web.mapper.DtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "1. Inventory")
public class StockController {

    private final StockQueries stockQueries;

    @GetMapping("/api/v1/inventory/{skuId}")
    @Operation(operationId = "inventory_1_get_by_sku", summary = "Get stock by SKU")
    public StockItemResponse bySku(@PathVariable @NotNull UUID skuId) {
        return DtoMapper.toDto(stockQueries.getBySkuId(skuId));
    }

    @GetMapping("/api/v1/inventory/product/{productId}")
    @Operation(operationId = "inventory_2_get_by_product", summary = "Get stocks by product")
    public List<StockItemResponse> byProduct(@PathVariable @NotNull UUID productId) {
        return stockQueries.getByProductId(productId).stream().map(DtoMapper::toDto).toList();
    }
}

