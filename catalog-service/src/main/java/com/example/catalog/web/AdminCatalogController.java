package com.example.catalog.web;

import com.example.catalog.application.brand.BrandCommands;
import com.example.catalog.application.category.CategoryCommands;
import com.example.catalog.application.product.ProductCommands;
import com.example.catalog.application.sku.SkuCommands;
import com.example.catalog.web.dto.*;
import com.example.catalog.web.mapper.DtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminCatalogController {

    private final CategoryCommands categoryCommands;
    private final BrandCommands brandCommands;
    private final ProductCommands productCommands;
    private final SkuCommands skuCommands;

    // ===== Category =====
    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:category:write')")
    public CategoryResponse createCategory(@RequestBody @Valid CategoryCreateRequest req) {
        var c = categoryCommands.create(req.name(), req.parentId(), req.active(), req.sortOrder());
        return DtoMapper.toDto(c);
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:category:write')")
    public CategoryResponse updateCategory(@PathVariable UUID id, @RequestBody @Valid CategoryUpdateRequest req) {
        var c = categoryCommands.update(id, req.name(), req.parentId(), req.active(), req.sortOrder());
        return DtoMapper.toDto(c);
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:category:delete')")
    public void deleteCategory(@PathVariable UUID id) {
        categoryCommands.delete(id);
    }

    // ===== Brand =====
    @PostMapping("/brands")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:brand:write')")
    public BrandResponse createBrand(@RequestBody @Valid BrandCreateRequest req) {
        var b = brandCommands.create(req.name(), req.active());
        return DtoMapper.toDto(b);
    }

    @PutMapping("/brands/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:brand:write')")
    public BrandResponse updateBrand(@PathVariable UUID id, @RequestBody @Valid BrandUpdateRequest req) {
        var b = brandCommands.update(id, req.name(), req.active());
        return DtoMapper.toDto(b);
    }

    @DeleteMapping("/brands/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:brand:delete')")
    public void deleteBrand(@PathVariable UUID id) {
        brandCommands.delete(id);
    }

    // ===== Product =====
    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:product:write')")
    public ProductDetailResponse createProduct(@RequestBody @Valid ProductCreateRequest req) {
        var p = productCommands.create(req.name(), req.shortDesc(), req.brandId(), req.categoryId(), req.published());
        return DtoMapper.toDetailDto(p);
    }

    @PutMapping("/products/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:product:write')")
    public ProductDetailResponse updateProduct(@PathVariable UUID id, @RequestBody @Valid ProductUpdateRequest req) {
        var p = productCommands.update(id, req.name(), req.shortDesc(), req.brandId(), req.categoryId(), req.published());
        return DtoMapper.toDetailDto(p);
    }

    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:product:delete')")
    public void deleteProduct(@PathVariable UUID id) {
        productCommands.delete(id);
    }

    // ===== SKU =====
    @PostMapping("/products/{productId}/skus")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:sku:write')")
    public SkuResponse createSku(@PathVariable UUID productId, @RequestBody @Valid SkuCreateRequest req) {
        var s = skuCommands.create(productId, req.skuCode(), req.active(), req.barcode());
        return DtoMapper.toDto(s);
    }

    @PutMapping("/skus/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:sku:write')")
    public SkuResponse updateSku(@PathVariable UUID id, @RequestBody @Valid SkuUpdateRequest req) {
        var s = skuCommands.update(id, req.skuCode(), req.active(), req.barcode());
        return DtoMapper.toDto(s);
    }

    @DeleteMapping("/skus/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:sku:delete')")
    public void deleteSku(@PathVariable UUID id) {
        skuCommands.delete(id);
    }
}
