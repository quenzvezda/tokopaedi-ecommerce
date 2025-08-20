package com.example.catalog.domain.product;

import com.example.catalog.domain.common.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    void deleteById(UUID id);
    PageResult<Product> search(ProductSearchCriteria criteria);
}
