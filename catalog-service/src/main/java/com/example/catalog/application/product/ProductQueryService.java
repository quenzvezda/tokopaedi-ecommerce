package com.example.catalog.application.product;

import com.example.catalog.domain.common.PageResult;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.product.ProductRepository;
import com.example.catalog.domain.product.ProductSearchCriteria;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class ProductQueryService implements ProductQueries {

    private final ProductRepository repo;

    @Override
    public PageResult<Product> search(String q, UUID brandId, UUID categoryId, int page, int size) {
        return repo.search(new ProductSearchCriteria(q, brandId, categoryId, page, size));
    }

    @Override
    public Product getById(UUID id) {
        return repo.findById(id).orElseThrow();
    }
}
