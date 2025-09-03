package com.example.inventory.infrastructure.jpa;

import com.example.inventory.domain.stock.ProcessedEvent;
import com.example.inventory.infrastructure.jpa.entity.JpaProcessedEvent;
import com.example.inventory.infrastructure.jpa.repository.JpaProcessedEventRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProcessedEventRepositoryImplTest {

    JpaProcessedEventRepository jpa = mock(JpaProcessedEventRepository.class);
    ProcessedEventRepositoryImpl repo = new ProcessedEventRepositoryImpl(jpa);

    @Test
    void existsById_delegates() {
        UUID id = UUID.randomUUID();
        when(jpa.existsById(id)).thenReturn(true);
        assertThat(repo.existsById(id)).isTrue();
        verify(jpa).existsById(id);
    }

    @Test
    void save_maps() {
        when(jpa.save(any())).thenAnswer(inv -> inv.getArgument(0));
        UUID id = UUID.randomUUID();
        repo.save(ProcessedEvent.builder().eventId(id).eventType("t").processedAt(Instant.now()).build());
        verify(jpa).save(any(JpaProcessedEvent.class));
    }
}

