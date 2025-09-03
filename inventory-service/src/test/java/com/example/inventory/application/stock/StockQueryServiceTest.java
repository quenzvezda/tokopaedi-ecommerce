package com.example.inventory.application.stock;

import com.example.inventory.domain.stock.StockItem;
import com.example.inventory.domain.stock.StockItemRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class StockQueryServiceTest {
    StockItemRepository repo = mock(StockItemRepository.class);
    StockQueryService svc = new StockQueryService(repo);

    @Test
    void getBySkuId_returnsItem() {
        UUID skuId = UUID.randomUUID();
        when(repo.findBySkuId(skuId)).thenReturn(Optional.of(StockItem.builder().skuId(skuId).build()));
        StockItem item = svc.getBySkuId(skuId);
        assertThat(item.getSkuId()).isEqualTo(skuId);
    }

    @Test
    void getBySkuId_notFound_throws() {
        UUID skuId = UUID.randomUUID();
        when(repo.findBySkuId(skuId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> svc.getBySkuId(skuId));
    }

    @Test
    void getByProductId_returnsList() {
        UUID pid = UUID.randomUUID();
        when(repo.findByProductId(pid)).thenReturn(List.of(
                StockItem.builder().skuId(UUID.randomUUID()).productId(pid).build()
        ));
        assertThat(svc.getByProductId(pid)).hasSize(1);
        verify(repo).findByProductId(pid);
    }
}

