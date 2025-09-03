package com.example.inventory.web;

import com.example.inventory.domain.stock.StockItem;
import com.example.inventory.web.dto.StockItemResponse;
import com.example.inventory.web.mapper.DtoMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DtoMapperTest {

    @Test
    void toDto_mapsAllFields() {
        UUID sku = UUID.randomUUID();
        UUID pid = UUID.randomUUID();
        var domain = StockItem.builder()
                .skuId(sku).productId(pid)
                .qtyOnHand(7).reserved(2).sellable(true)
                .build();
        StockItemResponse dto = DtoMapper.toDto(domain);
        assertThat(dto.skuId()).isEqualTo(sku);
        assertThat(dto.productId()).isEqualTo(pid);
        assertThat(dto.qtyOnHand()).isEqualTo(7);
        assertThat(dto.reserved()).isEqualTo(2);
        assertThat(dto.sellable()).isTrue();
    }
}

