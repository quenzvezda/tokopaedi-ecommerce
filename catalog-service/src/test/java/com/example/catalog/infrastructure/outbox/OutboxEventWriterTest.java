package com.example.catalog.infrastructure.outbox;

import com.example.catalog.application.sku.events.SkuActivated;
import com.example.catalog.application.sku.events.SkuCreated;
import com.example.catalog.application.sku.events.SkuDeactivated;
import com.example.catalog.infrastructure.jpa.entity.OutboxEventEntity;
import com.example.catalog.infrastructure.jpa.repository.JpaOutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OutboxEventWriterTest {

    JpaOutboxEventRepository repo;
    ObjectMapper objectMapper;
    OutboxEventWriter writer;

    UUID skuId;
    UUID productId;

    @BeforeEach
    void setUp() {
        repo = mock(JpaOutboxEventRepository.class);
        objectMapper = new ObjectMapper();
        writer = new OutboxEventWriter(repo, objectMapper);
        skuId = UUID.randomUUID();
        productId = UUID.randomUUID();
    }

    @Test
    void onSkuCreated_writesCreatedAndActivatedWhenActive() {
        SkuCreated evt = new SkuCreated(skuId, productId, true);
        writer.onSkuCreated(evt);

        ArgumentCaptor<OutboxEventEntity> captor = ArgumentCaptor.forClass(OutboxEventEntity.class);
        verify(repo, times(2)).save(captor.capture());
        assertThat(captor.getAllValues()).extracting(OutboxEventEntity::getEventType)
                .containsExactlyInAnyOrder("catalog.sku.created", "catalog.sku.activated");
        assertThat(captor.getAllValues()).allMatch(e -> skuId.toString().equals(e.getEventKey()));
    }

    @Test
    void onSkuActivated_writesEvent() {
        SkuActivated evt = new SkuActivated(skuId);
        writer.onSkuActivated(evt);
        ArgumentCaptor<OutboxEventEntity> captor = ArgumentCaptor.forClass(OutboxEventEntity.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getEventType()).isEqualTo("catalog.sku.activated");
    }

    @Test
    void onSkuDeactivated_writesEvent() {
        SkuDeactivated evt = new SkuDeactivated(skuId);
        writer.onSkuDeactivated(evt);
        ArgumentCaptor<OutboxEventEntity> captor = ArgumentCaptor.forClass(OutboxEventEntity.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getEventType()).isEqualTo("catalog.sku.deactivated");
    }
}

