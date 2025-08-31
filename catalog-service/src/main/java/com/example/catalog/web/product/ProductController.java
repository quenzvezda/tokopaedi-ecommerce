package com.example.catalog.web.product;

import com.example.catalog.application.product.ProductCommands;
import com.example.catalog.application.product.ProductQueries;
import com.example.catalog.domain.common.PageResult;
import com.example.catalog.web.dto.ProductCreateRequest;
import com.example.catalog.web.dto.ProductDetailResponse;
import com.example.catalog.web.dto.ProductListItemResponse;
import com.example.catalog.web.dto.ProductUpdateRequest;
import com.example.catalog.web.mapper.DtoMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Products")
public class ProductController {
    private final ProductQueries productQueries;
    private final ProductCommands productCommands;

    @GetMapping("/api/v1/catalog/products")
    @Operation(summary = "Search products")
    public PageResult<ProductListItemResponse> products(@RequestParam(required = false) String q,
                                                        @RequestParam(required = false) UUID brandId,
                                                        @RequestParam(required = false) UUID categoryId,
                                                        @RequestParam(defaultValue = "0") @Min(0) int page,
                                                        @RequestParam(defaultValue = "20") @Min(1) int size) {
        var pr = productQueries.search(q, brandId, categoryId, page, size);
        return new PageResult<>(
                pr.content().stream().map(DtoMapper::toListDto).toList(),
                pr.page(), pr.size(), pr.totalElements(), pr.totalPages()
        );
    }

    @GetMapping("/api/v1/catalog/products/{slug}")
    @Operation(summary = "Get product detail")
    public ProductDetailResponse productDetail(@PathVariable String slug) {
        return DtoMapper.toDetailDto(productQueries.getBySlug(slug));
    }

    @PostMapping("/api/v1/admin/products")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:product:write')")
    @Operation(summary = "Create product", security = {@SecurityRequirement(name = "bearer-key")})
    public ProductDetailResponse create(@RequestBody @Valid ProductCreateRequest req) {
        var p = productCommands.create(req.name(), req.shortDesc(), req.brandId(), req.categoryId(), req.published());
        return DtoMapper.toDetailDto(p);
    }

    @PutMapping("/api/v1/admin/products/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:product:write')")
    @Operation(summary = "Update product", security = {@SecurityRequirement(name = "bearer-key")})
    public ProductDetailResponse update(@PathVariable UUID id, @RequestBody @Valid ProductUpdateRequest req) {
        var p = productCommands.update(id, req.name(), req.shortDesc(), req.brandId(), req.categoryId(), req.published());
        return DtoMapper.toDetailDto(p);
    }

    @DeleteMapping("/api/v1/admin/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:product:delete')")
    @Operation(summary = "Delete product", security = {@SecurityRequirement(name = "bearer-key")})
    public void delete(@PathVariable UUID id) {
        productCommands.delete(id);
    }
}
