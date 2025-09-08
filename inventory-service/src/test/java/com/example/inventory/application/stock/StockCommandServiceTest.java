package com.example.inventory.application.stock;

import com.example.inventory.domain.stock.ProcessedEvent;
import com.example.inventory.domain.stock.ProcessedEventRepository;
import com.example.inventory.domain.stock.StockItem;
import com.example.inventory.domain.stock.StockItemRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class StockCommandServiceTest {

    StockItemRepository stockRepo = mock(StockItemRepository.class);
    ProcessedEventRepository eventRepo = mock(ProcessedEventRepository.class);
    StockCommandService svc = new StockCommandService(stockRepo, eventRepo);

    @Test
    void handleSkuCreated_idempotent_doesNothing() {
        UUID eventId = UUID.randomUUID();
        when(eventRepo.existsById(eventId)).thenReturn(true);

        svc.handleSkuCreated(eventId, UUID.randomUUID(), UUID.randomUUID());

        verify(stockRepo, never()).save(any());
        verify(eventRepo, never()).save(any());
    }

    @Test
    void handleSkuCreated_existingItem_onlyRecordEvent() {
        UUID eventId = UUID.randomUUID();
        UUID skuId = UUID.randomUUID();
        when(eventRepo.existsById(eventId)).thenReturn(false);
        when(stockRepo.findBySkuId(skuId)).thenReturn(Optional.of(StockItem.builder().skuId(skuId).build()));

        svc.handleSkuCreated(eventId, skuId, UUID.randomUUID());

        verify(stockRepo, never()).save(any());
        ArgumentCaptor<ProcessedEvent> cap = ArgumentCaptor.forClass(ProcessedEvent.class);
        verify(eventRepo).save(cap.capture());
        assertThat(cap.getValue().getEventId()).isEqualTo(eventId);
        assertThat(cap.getValue().getEventType()).isEqualTo("catalog.sku.created");
        assertThat(cap.getValue().getProcessedAt()).isNotNull();
    }

    @Test
    void handleSkuCreated_newItem_setsDefaults_andRecordsEvent() {
        UUID eventId = UUID.randomUUID();
        UUID skuId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        when(eventRepo.existsById(eventId)).thenReturn(false);
        when(stockRepo.findBySkuId(skuId)).thenReturn(Optional.empty());

        svc.handleSkuCreated(eventId, skuId, productId);

        ArgumentCaptor<StockItem> si = ArgumentCaptor.forClass(StockItem.class);
        verify(stockRepo).save(si.capture());
        StockItem saved = si.getValue();
        assertThat(saved.getSkuId()).isEqualTo(skuId);
        assertThat(saved.getProductId()).isEqualTo(productId);
        assertThat(saved.getQtyOnHand()).isZero();
        assertThat(saved.getReserved()).isZero();
        assertThat(saved.isSellable()).isFalse();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        verify(eventRepo).save(any());
    }

    @Test
    void handleSkuActivated_updatesSellableTrue_andRecordsEvent_whenExists() {
        UUID eventId = UUID.randomUUID();
        UUID skuId = UUID.randomUUID();
        when(eventRepo.existsById(eventId)).thenReturn(false);
        StockItem current = StockItem.builder()
                .skuId(skuId).qtyOnHand(5).reserved(1).sellable(false)
                .createdAt(Instant.now()).updatedAt(Instant.now()).build();
        when(stockRepo.findBySkuId(skuId)).thenReturn(Optional.of(current));

        svc.handleSkuActivated(eventId, skuId);

        ArgumentCaptor<StockItem> si = ArgumentCaptor.forClass(StockItem.class);
        verify(stockRepo).save(si.capture());
        assertThat(si.getValue().isSellable()).isTrue();
        assertThat(si.getValue().getUpdatedAt()).isNotNull();
        verify(eventRepo).save(argThat(e -> e.getEventType().equals("catalog.sku.activated")));
    }

    @Test
    void handleSkuActivated_createsWhenMissing_sellableTrue() {
        UUID eventId = UUID.randomUUID();
        UUID skuId = UUID.randomUUID();
        when(eventRepo.existsById(eventId)).thenReturn(false);
        when(stockRepo.findBySkuId(skuId)).thenReturn(Optional.empty());

        svc.handleSkuActivated(eventId, skuId);

        ArgumentCaptor<StockItem> si = ArgumentCaptor.forClass(StockItem.class);
        verify(stockRepo).save(si.capture());
        StockItem saved = si.getValue();
        assertThat(saved.getSkuId()).isEqualTo(skuId);
        assertThat(saved.isSellable()).isTrue();
        assertThat(saved.getQtyOnHand()).isZero();
        assertThat(saved.getReserved()).isZero();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        verify(eventRepo).save(any());
    }

    @Test
    void handleSkuActivated_idempotent_returnsEarly() {
        UUID eventId = UUID.randomUUID();
        when(eventRepo.existsById(eventId)).thenReturn(true);

        svc.handleSkuActivated(eventId, UUID.randomUUID());

        verify(stockRepo, never()).save(any());
        verify(eventRepo, never()).save(any());
    }

    @Test
    void handleSkuDeactivated_updatesSellableFalse_andRecordsEvent() {
        UUID eventId = UUID.randomUUID();
        UUID skuId = UUID.randomUUID();
        when(eventRepo.existsById(eventId)).thenReturn(false);
        when(stockRepo.findBySkuId(skuId)).thenReturn(Optional.of(
                StockItem.builder().skuId(skuId).sellable(true).createdAt(Instant.now()).updatedAt(Instant.now()).build()
        ));

        svc.handleSkuDeactivated(eventId, skuId);

        ArgumentCaptor<StockItem> si = ArgumentCaptor.forClass(StockItem.class);
        verify(stockRepo).save(si.capture());
        assertThat(si.getValue().isSellable()).isFalse();
        verify(eventRepo).save(argThat(e -> e.getEventType().equals("catalog.sku.deactivated")));
    }

    @Test
    void handleSkuDeactivated_createsWhenMissing_sellableFalse() {
        UUID eventId = UUID.randomUUID();
        UUID skuId = UUID.randomUUID();
        when(eventRepo.existsById(eventId)).thenReturn(false);
        when(stockRepo.findBySkuId(skuId)).thenReturn(Optional.empty());

        svc.handleSkuDeactivated(eventId, skuId);

        ArgumentCaptor<StockItem> si = ArgumentCaptor.forClass(StockItem.class);
        verify(stockRepo).save(si.capture());
        StockItem saved = si.getValue();
        assertThat(saved.getSkuId()).isEqualTo(skuId);
        assertThat(saved.isSellable()).isFalse();
        verify(eventRepo).save(any());
    }
}

