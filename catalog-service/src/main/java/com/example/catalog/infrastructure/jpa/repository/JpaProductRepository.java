package com.example.catalog.infrastructure.jpa.repository;

import com.example.catalog.infrastructure.jpa.entity.JpaProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface JpaProductRepository extends JpaRepository<JpaProduct, UUID>, JpaSpecificationExecutor<JpaProduct> {
    Page<JpaProduct> findAll(Pageable pageable);
    Optional<JpaProduct> findBySlug(String slug);
}
