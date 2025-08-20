package com.example.catalog.application.brand;

import com.example.catalog.domain.brand.Brand;

import java.util.List;

public interface BrandQueries {
    List<Brand> list(Boolean active);
}
