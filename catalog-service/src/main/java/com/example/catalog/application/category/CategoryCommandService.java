package com.example.catalog.application.category;

import com.example.catalog.domain.category.Category;
import com.example.catalog.domain.category.CategoryRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class CategoryCommandService implements CategoryCommands {

    private final CategoryRepository repo;

    @Override
    public Category create(String name, UUID parentId, Boolean active, Integer sortOrder) {
        Category c = Category.builder()
                .id(UUID.randomUUID())
                .name(name)
                .parentId(parentId)
                .active(active != null ? active : true)
                .sortOrder(sortOrder)
                .build();
        return repo.save(c);
    }

    @Override
    public Category update(UUID id, String name, UUID parentId, Boolean active, Integer sortOrder) {
        Category current = repo.findById(id).orElseThrow();
        current.setName(name != null ? name : current.getName());
        current.setParentId(parentId != null ? parentId : current.getParentId());
        current.setActive(active != null ? active : current.isActive());
        current.setSortOrder(sortOrder != null ? sortOrder : current.getSortOrder());
        return repo.save(current);
    }

    @Override
    public void delete(UUID id) {
        repo.deleteById(id);
    }
}

