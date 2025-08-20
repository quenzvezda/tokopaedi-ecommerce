package com.example.catalog.domain.category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(UUID id);
    void deleteById(UUID id);
    List<Category> findByParent(UUID parentId, Boolean active);
    List<Category> findAll(Boolean active);
}
