package com.example.catalog.infrastructure.jpa;

import com.example.catalog.domain.sku.Sku;
import com.example.catalog.domain.sku.SkuRepository;
import com.example.catalog.infrastructure.jpa.mapper.JpaMapper;
import com.example.catalog.infrastructure.jpa.repository.JpaSkuRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class SkuRepositoryImpl implements SkuRepository {

    private final JpaSkuRepository jpa;

    @Override
    public Sku save(Sku sku) {
        return JpaMapper.toDomain(jpa.save(JpaMapper.toJpa(sku)));
    }

    @Override
    public Optional<Sku> findById(UUID id) {
        return jpa.findById(id).map(JpaMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    public List<Sku> findByProductId(UUID productId) {
        return jpa.findByProductId(productId).stream().map(JpaMapper::toDomain).toList();
    }
}
