package com.example.inventory.infrastructure.jpa.repository;

import com.example.inventory.infrastructure.jpa.entity.JpaProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaProcessedEventRepository extends JpaRepository<JpaProcessedEvent, UUID> {
}

