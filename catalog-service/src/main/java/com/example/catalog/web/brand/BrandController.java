package com.example.catalog.web.brand;

import com.example.catalog.application.brand.BrandCommands;
import com.example.catalog.application.brand.BrandQueries;
import com.example.catalog_service.web.api.BrandApi;
import com.example.catalog_service.web.model.Brand;
import com.example.catalog_service.web.model.BrandCreateRequest;
import com.example.catalog_service.web.model.BrandUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class BrandController implements BrandApi {
	private final BrandQueries brandQueries;
	private final BrandCommands brandCommands;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('catalog:brand:write')")
    public ResponseEntity<Brand> createBrand(@Valid BrandCreateRequest brandCreateRequest) {
        var b = brandCommands.create(brandCreateRequest.getName(), brandCreateRequest.getActive());
        return ResponseEntity.status(201).body(map(b));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('catalog:brand:write')")
    public ResponseEntity<Void> deleteBrand(UUID id) {
        brandCommands.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<Brand>> listBrands(Boolean active) {
        return ResponseEntity.ok(brandQueries.list(active).stream()
                .map(BrandController::map)
                .toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('catalog:brand:write')")
    public ResponseEntity<Brand> updateBrand(UUID id, @Valid BrandUpdateRequest brandUpdateRequest) {
        var b = brandCommands.update(id, brandUpdateRequest.getName(), brandUpdateRequest.getActive());
        return ResponseEntity.ok(map(b));
    }

    private static Brand map(com.example.catalog.domain.brand.Brand b) {
        return new Brand()
                .id(b.getId())
                .name(b.getName())
                .active(b.isActive());
    }
}
