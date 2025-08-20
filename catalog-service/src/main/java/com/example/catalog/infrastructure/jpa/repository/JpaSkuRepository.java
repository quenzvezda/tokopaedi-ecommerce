package com.example.catalog.infrastructure.jpa.repository;

import com.example.catalog.infrastructure.jpa.entity.JpaSku;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaSkuRepository extends JpaRepository<JpaSku, UUID> {
    List<JpaSku> findByProductId(UUID productId);
}
