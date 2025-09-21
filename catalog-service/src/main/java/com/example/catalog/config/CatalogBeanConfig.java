package com.example.catalog.config;

import com.example.catalog.application.brand.*;
import com.example.catalog.application.category.*;
import com.example.catalog.application.product.*;
import com.example.catalog.application.sku.*;
import com.example.catalog.domain.brand.BrandRepository;
import com.example.catalog.domain.category.CategoryRepository;
import com.example.catalog.domain.product.ProductRepository;
import com.example.catalog.domain.sku.SkuRepository;
import com.example.catalog.infrastructure.jpa.*;
import com.example.catalog.infrastructure.jpa.repository.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CatalogBeanConfig {

    // Adapters (Domain Repos -> JPA)
    @Bean
    CategoryRepository categoryRepository(JpaCategoryRepository jpa) {
        return new CategoryRepositoryImpl(jpa);
    }

    @Bean
    BrandRepository brandRepository(JpaBrandRepository jpa) {
        return new BrandRepositoryImpl(jpa);
    }

    @Bean
    ProductRepository productRepository(JpaProductRepository jpa) {
        return new ProductRepositoryImpl(jpa);
    }

    @Bean
    SkuRepository skuRepository(JpaSkuRepository jpa) {
        return new SkuRepositoryImpl(jpa);
    }

    // Use cases (Factory Pattern)
    @Bean
    CategoryCommands categoryCommands(CategoryRepository repo) {
        return new CategoryCommandService(repo);
    }

    @Bean
    CategoryQueries categoryQueries(CategoryRepository repo) {
        return new CategoryQueryService(repo);
    }

    @Bean
    BrandCommands brandCommands(BrandRepository repo) {
        return new BrandCommandService(repo);
    }

    @Bean
    BrandQueries brandQueries(BrandRepository repo) {
        return new BrandQueryService(repo);
    }

    @Bean
    ProductCommands productCommands(ProductRepository repo) {
        return new ProductCommandService(repo);
    }

    @Bean
    ProductQueries productQueries(ProductRepository repo) {
        return new ProductQueryService(repo);
    }

    @Bean
    SkuCommands skuCommands(SkuRepository repo, ProductRepository productRepository, ApplicationEventPublisher events) {
        return new SkuCommandService(repo, productRepository, events);
    }

    @Bean
    SkuQueries skuQueries(SkuRepository repo) {
        return new SkuQueryService(repo);
    }
}
