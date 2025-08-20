package com.example.catalog.application.category;

import com.example.catalog.domain.category.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryQueries {
    List<Category> list(Boolean active, UUID parentId);
}
