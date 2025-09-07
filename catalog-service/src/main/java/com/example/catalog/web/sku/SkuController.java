package com.example.catalog.web.sku;

import com.example.catalog.application.sku.SkuCommands;
import com.example.catalog_service.web.api.SkuApi;
import com.example.catalog_service.web.model.Sku;
import com.example.catalog_service.web.model.SkuCreateRequest;
import com.example.catalog_service.web.model.SkuUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SkuController implements SkuApi {
	private final SkuCommands skuCommands;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('catalog:sku:write')")
    public ResponseEntity<Sku> createSku(UUID productId, @Valid SkuCreateRequest skuCreateRequest) {
        var s = skuCommands.create(productId, skuCreateRequest.getSkuCode(), skuCreateRequest.getActive(), skuCreateRequest.getBarcode());
        return ResponseEntity.status(201).body(map(s));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('catalog:sku:write')")
    public ResponseEntity<Void> deleteSku(UUID id) {
        skuCommands.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('catalog:sku:write')")
    public ResponseEntity<Sku> updateSku(UUID id, @Valid SkuUpdateRequest skuUpdateRequest) {
        var s = skuCommands.update(id, skuUpdateRequest.getSkuCode(), skuUpdateRequest.getActive(), skuUpdateRequest.getBarcode());
        return ResponseEntity.ok(map(s));
    }

    private static Sku map(com.example.catalog.domain.sku.Sku s) {
        return new Sku()
                .id(s.getId())
                .productId(s.getProductId())
                .skuCode(s.getSkuCode())
                .active(s.isActive())
                .barcode(s.getBarcode());
    }
}
