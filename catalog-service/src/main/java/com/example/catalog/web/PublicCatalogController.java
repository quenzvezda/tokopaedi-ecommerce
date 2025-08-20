package com.example.catalog.web;

import com.example.catalog.application.brand.BrandQueries;
import com.example.catalog.application.category.CategoryQueries;
import com.example.catalog.application.product.ProductQueries;
import com.example.catalog.domain.common.PageResult;
import com.example.catalog.web.dto.BrandResponse;
import com.example.catalog.web.dto.CategoryResponse;
import com.example.catalog.web.dto.ProductListItemResponse;
import com.example.catalog.web.dto.ProductDetailResponse;
import com.example.catalog.web.mapper.DtoMapper;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v1/catalog")
public class PublicCatalogController {

    private final CategoryQueries categoryQueries;
    private final BrandQueries brandQueries;
    private final ProductQueries productQueries;

    public PublicCatalogController(CategoryQueries categoryQueries,
                                   BrandQueries brandQueries,
                                   ProductQueries productQueries) {
        this.categoryQueries = categoryQueries;
        this.brandQueries = brandQueries;
        this.productQueries = productQueries;
    }

    @GetMapping("/categories")
    public List<CategoryResponse> categories(@RequestParam(required = false) Boolean active,
                                             @RequestParam(required = false) UUID parentId) {
        return categoryQueries.list(active, parentId).stream().map(DtoMapper::toDto).toList();
    }

    @GetMapping("/brands")
    public List<BrandResponse> brands(@RequestParam(required = false) Boolean active) {
        return brandQueries.list(active).stream().map(DtoMapper::toDto).toList();
    }

    @GetMapping("/products")
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

    @GetMapping("/products/{id}")
    public ProductDetailResponse productDetail(@PathVariable UUID id) {
        return DtoMapper.toDetailDto(productQueries.getById(id));
    }
}
