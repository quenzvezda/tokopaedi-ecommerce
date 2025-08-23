package com.example.catalog.application.category;

import com.example.catalog.domain.category.Category;

import java.util.UUID;

public interface CategoryCommands {
    Category create(String name, UUID parentId, Boolean active, Integer sortOrder);
    Category update(UUID id, String name, UUID parentId, Boolean active, Integer sortOrder);
    void delete(UUID id);
}

