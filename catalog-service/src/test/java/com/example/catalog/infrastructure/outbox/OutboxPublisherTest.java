package com.example.catalog.infrastructure.outbox;

import com.example.catalog.infrastructure.jpa.entity.OutboxEventEntity;
import com.example.catalog.infrastructure.jpa.repository.JpaOutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OutboxPublisherTest {

    @Test
    void publishPending_publishesAndMarksPublished() throws Exception {
        JpaOutboxEventRepository repo = mock(JpaOutboxEventRepository.class);
        KafkaTemplate<String, String> kt = mock(KafkaTemplate.class);
        when(kt.send(anyString(), anyString(), anyString())).thenReturn(completedFuture());

        var event = OutboxEventEntity.builder()
                .id(UUID.randomUUID())
                .aggregateType("catalog")
                .aggregateId(UUID.randomUUID())
                .eventType("catalog.sku.created")
                .eventKey(UUID.randomUUID().toString())
                .payload("{\"x\":1}")
                .createdAt(Instant.now())
                .attempts(0)
                .build();

        when(repo.findTop100ByPublishedAtIsNullOrderByCreatedAtAsc()).thenReturn(List.of(event));

        var publisher = new OutboxPublisher(repo, kt);
        publisher.publishPending();

        verify(kt).send(eq("catalog-events"), eq(event.getEventKey()), eq(event.getPayload()));

        ArgumentCaptor<OutboxEventEntity> saved = ArgumentCaptor.forClass(OutboxEventEntity.class);
        verify(repo, atLeastOnce()).save(saved.capture());
        assertThat(saved.getValue().getPublishedAt()).isNotNull();
    }

    private static <T> java.util.concurrent.CompletableFuture<T> completedFuture() {
        var f = new java.util.concurrent.CompletableFuture<T>();
        f.complete(null);
        return f;
    }
}

