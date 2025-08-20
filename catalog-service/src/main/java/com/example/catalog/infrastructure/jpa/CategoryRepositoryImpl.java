package com.example.catalog.infrastructure.jpa;

import com.example.catalog.domain.category.Category;
import com.example.catalog.domain.category.CategoryRepository;
import com.example.catalog.infrastructure.jpa.mapper.JpaMapper;
import com.example.catalog.infrastructure.jpa.repository.JpaCategoryRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final JpaCategoryRepository jpa;

    @Override
    public Category save(Category category) {
        return JpaMapper.toDomain(jpa.save(JpaMapper.toJpa(category)));
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return jpa.findById(id).map(JpaMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    public List<Category> findByParent(UUID parentId, Boolean active) {
        if (active == null) return jpa.findByParentId(parentId).stream().map(JpaMapper::toDomain).toList();
        return jpa.findByParentIdAndActive(parentId, active).stream().map(JpaMapper::toDomain).toList();
    }

    @Override
    public List<Category> findAll(Boolean active) {
        if (active == null) return jpa.findAll().stream().map(JpaMapper::toDomain).toList();
        return jpa.findByActive(active).stream().map(JpaMapper::toDomain).toList();
    }
}
