package com.example.catalog.application.sku;

import com.example.catalog.domain.sku.Sku;
import com.example.catalog.domain.sku.SkuRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class SkuCommandService implements SkuCommands {

    private final SkuRepository repo;

    @Override
    public Sku create(UUID productId, String skuCode, Boolean active, String barcode) {
        Sku s = Sku.builder()
                .id(UUID.randomUUID())
                .productId(productId)
                .skuCode(skuCode)
                .active(active != null ? active : true)
                .barcode(barcode)
                .build();
        return repo.save(s);
    }

    @Override
    public Sku update(UUID id, String skuCode, Boolean active, String barcode) {
        Sku current = repo.findById(id).orElseThrow();
        current.setSkuCode(skuCode != null ? skuCode : current.getSkuCode());
        current.setActive(active != null ? active : current.isActive());
        current.setBarcode(barcode != null ? barcode : current.getBarcode());
        return repo.save(current);
    }

    @Override
    public void delete(UUID id) {
        repo.deleteById(id);
    }
}

