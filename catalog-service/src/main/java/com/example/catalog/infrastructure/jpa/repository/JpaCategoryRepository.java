package com.example.catalog.infrastructure.jpa.repository;

import com.example.catalog.infrastructure.jpa.entity.JpaCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaCategoryRepository extends JpaRepository<JpaCategory, UUID> {
    List<JpaCategory> findByActive(boolean active);
    List<JpaCategory> findByParentId(UUID parentId);
    List<JpaCategory> findByParentIdAndActive(UUID parentId, boolean active);
}
