package com.example.catalog.config;

import com.example.catalog.application.brand.*;
import com.example.catalog.application.category.*;
import com.example.catalog.application.product.*;
import com.example.catalog.application.sku.*;
import com.example.catalog.domain.brand.BrandRepository;
import com.example.catalog.domain.category.CategoryRepository;
import com.example.catalog.domain.product.ProductRepository;
import com.example.catalog.domain.sku.SkuRepository;
import com.example.catalog.infrastructure.jpa.repository.*;
import com.example.catalog.security.IamEntitlementsClient;
import com.example.catalog.application.authz.EntitlementsQuery;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@ContextConfiguration(classes = {CatalogBeanConfig.class, AuthzBeanConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class BeanConfigWiringTest {

    @MockBean JpaCategoryRepository jpaCategoryRepository;
    @MockBean JpaBrandRepository jpaBrandRepository;
    @MockBean JpaProductRepository jpaProductRepository;
    @MockBean JpaSkuRepository jpaSkuRepository;
    @MockBean IamEntitlementsClient iamEntitlementsClient;

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final SkuRepository skuRepository;

    private final CategoryCommands categoryCommands;
    private final CategoryQueries categoryQueries;
    private final BrandCommands brandCommands;
    private final BrandQueries brandQueries;
    private final ProductCommands productCommands;
    private final ProductQueries productQueries;
    private final SkuCommands skuCommands;
    private final SkuQueries skuQueries;

    private final EntitlementsQuery entitlementsQuery;

    BeanConfigWiringTest(CategoryRepository categoryRepository,
                         BrandRepository brandRepository,
                         ProductRepository productRepository,
                         SkuRepository skuRepository,
                         CategoryCommands categoryCommands,
                         CategoryQueries categoryQueries,
                         BrandCommands brandCommands,
                         BrandQueries brandQueries,
                         ProductCommands productCommands,
                         ProductQueries productQueries,
                         SkuCommands skuCommands,
                         SkuQueries skuQueries,
                         EntitlementsQuery entitlementsQuery) {
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.productRepository = productRepository;
        this.skuRepository = skuRepository;
        this.categoryCommands = categoryCommands;
        this.categoryQueries = categoryQueries;
        this.brandCommands = brandCommands;
        this.brandQueries = brandQueries;
        this.productCommands = productCommands;
        this.productQueries = productQueries;
        this.skuCommands = skuCommands;
        this.skuQueries = skuQueries;
        this.entitlementsQuery = entitlementsQuery;
    }

    @Test
    void beans_present() {
        assertThat(categoryRepository).isNotNull();
        assertThat(brandRepository).isNotNull();
        assertThat(productRepository).isNotNull();
        assertThat(skuRepository).isNotNull();

        assertThat(categoryCommands).isNotNull();
        assertThat(categoryQueries).isNotNull();
        assertThat(brandCommands).isNotNull();
        assertThat(brandQueries).isNotNull();
        assertThat(productCommands).isNotNull();
        assertThat(productQueries).isNotNull();
        assertThat(skuCommands).isNotNull();
        assertThat(skuQueries).isNotNull();
        assertThat(entitlementsQuery).isNotNull();
    }
}

