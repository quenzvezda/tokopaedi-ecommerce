package com.example.catalog.application.brand;

import com.example.catalog.domain.brand.Brand;
import com.example.catalog.domain.brand.BrandRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BrandQueryService implements BrandQueries {
    private final BrandRepository repo;

    @Override
    public List<Brand> list(Boolean active) {
        return repo.findAll(active);
    }
}
