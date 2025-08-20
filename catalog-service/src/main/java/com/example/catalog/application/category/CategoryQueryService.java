package com.example.catalog.application.category;

import com.example.catalog.domain.category.Category;
import com.example.catalog.domain.category.CategoryRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class CategoryQueryService implements CategoryQueries {

    private final CategoryRepository repo;

    @Override
    public List<Category> list(Boolean active, UUID parentId) {
        if (parentId != null) return repo.findByParent(parentId, active);
        return repo.findAll(active);
    }
}
