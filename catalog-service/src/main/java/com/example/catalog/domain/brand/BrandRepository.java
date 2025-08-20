package com.example.catalog.domain.brand;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BrandRepository {
    Brand save(Brand brand);
    Optional<Brand> findById(UUID id);
    void deleteById(UUID id);
    List<Brand> findAll(Boolean active);
}
