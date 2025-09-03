package com.example.inventory.domain.stock;

import java.util.UUID;

public interface ProcessedEventRepository {
    boolean existsById(UUID eventId);
    void save(ProcessedEvent event);
}

