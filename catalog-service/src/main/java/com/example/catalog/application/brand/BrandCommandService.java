package com.example.catalog.application.brand;

import com.example.catalog.domain.brand.Brand;
import com.example.catalog.domain.brand.BrandRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class BrandCommandService implements BrandCommands {
    private final BrandRepository repo;

    @Override
    public Brand create(String name, Boolean active) {
        Brand b = Brand.builder()
                .id(UUID.randomUUID())
                .name(name)
                .active(active != null ? active : true)
                .build();
        return repo.save(b);
    }

    @Override
    public Brand update(UUID id, String name, Boolean active) {
        Brand current = repo.findById(id).orElseThrow();
        current.setName(name != null ? name : current.getName());
        current.setActive(active != null ? active : current.isActive());
        return repo.save(current);
    }

    @Override
    public void delete(UUID id) {
        repo.deleteById(id);
    }
}

