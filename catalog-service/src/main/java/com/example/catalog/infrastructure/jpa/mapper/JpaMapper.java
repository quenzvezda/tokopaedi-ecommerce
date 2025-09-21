package com.example.catalog.infrastructure.jpa.mapper;

import com.example.catalog.domain.brand.Brand;
import com.example.catalog.domain.category.Category;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.sku.Sku;
import com.example.catalog.infrastructure.jpa.entity.JpaBrand;
import com.example.catalog.infrastructure.jpa.entity.JpaCategory;
import com.example.catalog.infrastructure.jpa.entity.JpaProduct;
import com.example.catalog.infrastructure.jpa.entity.JpaSku;

public final class JpaMapper {
    private JpaMapper() {}

    public static JpaCategory toJpa(Category c) {
        return JpaCategory.builder()
                .id(c.getId())
                .parentId(c.getParentId())
                .name(c.getName())
                .active(c.isActive())
                .sortOrder(c.getSortOrder())
                .build();
    }

    public static Category toDomain(JpaCategory j) {
        return Category.builder()
                .id(j.getId())
                .parentId(j.getParentId())
                .name(j.getName())
                .active(j.isActive())
                .sortOrder(j.getSortOrder())
                .build();
    }

    public static JpaBrand toJpa(Brand b) {
        return JpaBrand.builder()
                .id(b.getId())
                .name(b.getName())
                .active(b.isActive())
                .build();
    }

    public static Brand toDomain(JpaBrand j) {
        return Brand.builder()
                .id(j.getId())
                .name(j.getName())
                .active(j.isActive())
                .build();
    }

    public static JpaProduct toJpa(Product p) {
        return JpaProduct.builder()
                .id(p.getId())
                .name(p.getName())
                .slug(p.getSlug())
                .shortDesc(p.getShortDesc())
                .brandId(p.getBrandId())
                .categoryId(p.getCategoryId())
                .published(p.isPublished())
                .createdBy(p.getCreatedBy())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    public static Product toDomain(JpaProduct j) {
        return Product.builder()
                .id(j.getId())
                .name(j.getName())
                .slug(j.getSlug())
                .shortDesc(j.getShortDesc())
                .brandId(j.getBrandId())
                .categoryId(j.getCategoryId())
                .published(j.isPublished())
                .createdBy(j.getCreatedBy())
                .createdAt(j.getCreatedAt())
                .updatedAt(j.getUpdatedAt())
                .build();
    }

    public static JpaSku toJpa(Sku s) {
        return JpaSku.builder()
                .id(s.getId())
                .productId(s.getProductId())
                .skuCode(s.getSkuCode())
                .active(s.isActive())
                .barcode(s.getBarcode())
                .build();
    }

    public static Sku toDomain(JpaSku j) {
        return Sku.builder()
                .id(j.getId())
                .productId(j.getProductId())
                .skuCode(j.getSkuCode())
                .active(j.isActive())
                .barcode(j.getBarcode())
                .build();
    }
}
