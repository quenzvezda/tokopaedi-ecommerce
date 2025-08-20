package com.example.catalog.infrastructure.jpa;

import com.example.catalog.domain.common.PageResult;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.product.ProductRepository;
import com.example.catalog.domain.product.ProductSearchCriteria;
import com.example.catalog.infrastructure.jpa.entity.JpaProduct;
import com.example.catalog.infrastructure.jpa.mapper.JpaMapper;
import com.example.catalog.infrastructure.jpa.repository.JpaProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpa;

    @Override
    public Product save(Product product) {
        return JpaMapper.toDomain(jpa.save(JpaMapper.toJpa(product)));
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpa.findById(id).map(JpaMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    public PageResult<Product> search(ProductSearchCriteria c) {
        var pageable = PageRequest.of(Math.max(0, c.page()), Math.max(1, c.size()));
        Specification<JpaProduct> spec = Specification.where(null);

        if (c.normalizedQuery() != null && !c.normalizedQuery().isBlank()) {
            spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("name")), "%" + c.normalizedQuery().toLowerCase() + "%"));
        }
        if (c.brandId() != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("brandId"), c.brandId()));
        }
        if (c.categoryId() != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("categoryId"), c.categoryId()));
        }

        var page = jpa.findAll(spec, pageable);
        var content = page.getContent().stream().map(JpaMapper::toDomain).toList();
        return new PageResult<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}
