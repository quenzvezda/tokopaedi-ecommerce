package com.example.catalog.infrastructure.jpa;

import com.example.catalog.domain.brand.Brand;
import com.example.catalog.domain.brand.BrandRepository;
import com.example.catalog.infrastructure.jpa.mapper.JpaMapper;
import com.example.catalog.infrastructure.jpa.repository.JpaBrandRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class BrandRepositoryImpl implements BrandRepository {

    private final JpaBrandRepository jpa;

    @Override
    public Brand save(Brand brand) {
        return JpaMapper.toDomain(jpa.save(JpaMapper.toJpa(brand)));
    }

    @Override
    public Optional<Brand> findById(UUID id) {
        return jpa.findById(id).map(JpaMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    public List<Brand> findAll(Boolean active) {
        if (active == null) return jpa.findAll().stream().map(JpaMapper::toDomain).toList();
        return jpa.findByActive(active).stream().map(JpaMapper::toDomain).toList();
    }
}
