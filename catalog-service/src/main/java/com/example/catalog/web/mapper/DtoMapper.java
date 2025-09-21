package com.example.catalog.web.mapper;

import com.example.catalog.domain.brand.Brand;
import com.example.catalog.domain.category.Category;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.sku.Sku;
import com.example.catalog.web.dto.*;

public final class DtoMapper {

    private DtoMapper() {}

    public static CategoryResponse toDto(Category c) {
        return new CategoryResponse(c.getId(), c.getParentId(), c.getName(), c.isActive(), c.getSortOrder());
    }

    public static BrandResponse toDto(Brand b) {
        return new BrandResponse(b.getId(), b.getName(), b.isActive());
    }

    public static ProductListItemResponse toListDto(Product p) {
        return new ProductListItemResponse(
                p.getId(), p.getName(), p.getSlug(), p.getShortDesc(),
                p.getBrandId(), p.getCategoryId(), p.isPublished(),
                p.getCreatedBy(),
                p.getCreatedAt()
        );
    }

    public static ProductDetailResponse toDetailDto(Product p) {
        return new ProductDetailResponse(
                p.getId(), p.getName(), p.getSlug(), p.getShortDesc(), p.getBrandId(), p.getCategoryId(),
                p.isPublished(),
                p.getCreatedBy(),
                p.getCreatedAt(), p.getUpdatedAt()
        );
    }

    public static SkuResponse toDto(Sku s) {
        return new SkuResponse(s.getId(), s.getProductId(), s.getSkuCode(), s.isActive(), s.getBarcode());
    }
}
