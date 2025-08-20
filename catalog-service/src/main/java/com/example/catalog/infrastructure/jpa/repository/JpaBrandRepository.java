package com.example.catalog.infrastructure.jpa.repository;

import com.example.catalog.infrastructure.jpa.entity.JpaBrand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaBrandRepository extends JpaRepository<JpaBrand, UUID> {
    List<JpaBrand> findByActive(boolean active);
}
