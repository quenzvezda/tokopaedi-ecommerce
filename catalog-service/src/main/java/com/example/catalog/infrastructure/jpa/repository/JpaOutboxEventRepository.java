package com.example.catalog.infrastructure.jpa.repository;

import com.example.catalog.infrastructure.jpa.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaOutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {
    List<OutboxEventEntity> findTop100ByPublishedAtIsNullOrderByCreatedAtAsc();
}

