package com.example.inventory.infrastructure.jpa;

import com.example.inventory.domain.stock.ProcessedEvent;
import com.example.inventory.domain.stock.ProcessedEventRepository;
import com.example.inventory.infrastructure.jpa.mapper.JpaMapper;
import com.example.inventory.infrastructure.jpa.repository.JpaProcessedEventRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class ProcessedEventRepositoryImpl implements ProcessedEventRepository {

    private final JpaProcessedEventRepository jpa;

    @Override
    public boolean existsById(UUID eventId) {
        return jpa.existsById(eventId);
    }

    @Override
    public void save(ProcessedEvent event) {
        jpa.save(JpaMapper.toJpa(event));
    }
}

