package com.example.catalog.application.product;

import com.example.catalog.domain.common.PageResult;
import com.example.catalog.domain.product.Product;

import java.util.UUID;

public interface ProductQueries {
    PageResult<Product> search(String q, UUID brandId, UUID categoryId, int page, int size);
    Product getById(UUID id);
}
