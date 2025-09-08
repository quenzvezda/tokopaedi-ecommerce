package com.example.inventory.infrastructure.jpa;

import com.example.inventory.domain.stock.StockItem;
import com.example.inventory.infrastructure.jpa.entity.JpaStockItem;
import com.example.inventory.infrastructure.jpa.repository.JpaStockItemRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class StockItemRepositoryImplTest {

    JpaStockItemRepository jpa = mock(JpaStockItemRepository.class);
    StockItemRepositoryImpl repo = new StockItemRepositoryImpl(jpa);

    @Test
    void save_maps() {
        UUID skuId = UUID.randomUUID();
        when(jpa.save(any())).thenReturn(JpaStockItem.builder()
                .skuId(skuId)
                .productId(UUID.randomUUID())
                .qtyOnHand(0)
                .reserved(0)
                .sellable(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        StockItem res = repo.save(StockItem.builder().skuId(skuId).productId(UUID.randomUUID())
                .qtyOnHand(0).reserved(0).sellable(false).createdAt(Instant.now()).updatedAt(Instant.now()).build());
        assertThat(res.getSkuId()).isEqualTo(skuId);
    }

    @Test
    void findByProductId_mapsList() {
        UUID pid = UUID.randomUUID();
        when(jpa.findByProductId(pid)).thenReturn(List.of(JpaStockItem.builder()
                .skuId(UUID.randomUUID())
                .productId(pid)
                .qtyOnHand(0)
                .reserved(0)
                .sellable(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build()));
        var list = repo.findByProductId(pid);
        assertThat(list).hasSize(1);
        verify(jpa).findByProductId(pid);
    }

    @Test
    void findBySkuId_mapsOptional() {
        UUID sku = UUID.randomUUID();
        when(jpa.findById(sku)).thenReturn(Optional.of(JpaStockItem.builder()
                .skuId(sku)
                .productId(UUID.randomUUID())
                .qtyOnHand(0)
                .reserved(0)
                .sellable(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build()));
        assertThat(repo.findBySkuId(sku)).isPresent();
    }
}

