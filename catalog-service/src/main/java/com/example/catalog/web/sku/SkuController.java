package com.example.catalog.web.sku;

import com.example.catalog.application.sku.SkuCommands;
import com.example.catalog.web.dto.SkuCreateRequest;
import com.example.catalog.web.dto.SkuResponse;
import com.example.catalog.web.dto.SkuUpdateRequest;
import com.example.catalog.web.mapper.DtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SkuController {
    private final SkuCommands skuCommands;

    @PostMapping("/api/v1/admin/products/{productId}/skus")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:sku:write')")
    public SkuResponse create(@PathVariable UUID productId, @RequestBody @Valid SkuCreateRequest req) {
        var s = skuCommands.create(productId, req.skuCode(), req.active(), req.barcode());
        return DtoMapper.toDto(s);
    }

    @PutMapping("/api/v1/admin/skus/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:sku:write')")
    public SkuResponse update(@PathVariable UUID id, @RequestBody @Valid SkuUpdateRequest req) {
        var s = skuCommands.update(id, req.skuCode(), req.active(), req.barcode());
        return DtoMapper.toDto(s);
    }

    @DeleteMapping("/api/v1/admin/skus/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:sku:delete')")
    public void delete(@PathVariable UUID id) {
        skuCommands.delete(id);
    }
}
