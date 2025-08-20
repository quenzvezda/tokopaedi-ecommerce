package com.example.catalog.application.product;

import com.example.catalog.domain.common.PageResult;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.product.ProductRepository;
import com.example.catalog.domain.product.ProductSearchCriteria;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class ProductCommandService implements ProductCommands {

    private final ProductRepository repo;

    @Override
    public Product create(String name, String shortDesc, UUID brandId, UUID categoryId, Boolean published) {
        Product p = Product.builder()
                .id(UUID.randomUUID())
                .name(name)
                .shortDesc(shortDesc)
                .brandId(brandId)
                .categoryId(categoryId)
                .published(Boolean.TRUE.equals(published))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return repo.save(p);
    }

    @Override
    public Product update(UUID id, String name, String shortDesc, UUID brandId, UUID categoryId, Boolean published) {
        Product current = repo.findById(id).orElseThrow();
        current.setName(name != null ? name : current.getName());
        current.setShortDesc(shortDesc != null ? shortDesc : current.getShortDesc());
        current.setBrandId(brandId != null ? brandId : current.getBrandId());
        current.setCategoryId(categoryId != null ? categoryId : current.getCategoryId());
        if (published != null) current.setPublished(published);
        current.setUpdatedAt(Instant.now());
        return repo.save(current);
    }

    @Override
    public void delete(UUID id) {
        repo.deleteById(id);
    }
}

