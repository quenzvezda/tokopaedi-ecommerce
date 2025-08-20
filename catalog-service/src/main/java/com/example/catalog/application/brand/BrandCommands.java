package com.example.catalog.application.brand;

import com.example.catalog.domain.brand.Brand;

import java.util.List;
import java.util.UUID;

public interface BrandCommands {
    Brand create(String name, Boolean active);
    Brand update(UUID id, String name, Boolean active);
    void delete(UUID id);
}

