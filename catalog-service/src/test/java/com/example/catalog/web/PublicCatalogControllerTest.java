package com.example.catalog.web;

import com.example.catalog.application.brand.BrandQueries;
import com.example.catalog.application.category.CategoryQueries;
import com.example.catalog.application.product.ProductQueries;
import com.example.catalog.domain.brand.Brand;
import com.example.catalog.domain.category.Category;
import com.example.catalog.domain.common.PageResult;
import com.example.catalog.domain.product.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PublicCatalogController.class)
@Import({GlobalExceptionHandler.class, WebTestConfig.class})
class PublicCatalogControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean CategoryQueries categoryQueries;
    @MockBean BrandQueries brandQueries;
    @MockBean ProductQueries productQueries;

    private final ObjectMapper om = new ObjectMapper();

    @Test
    void categories_ok() throws Exception {
        when(categoryQueries.list(null, null)).thenReturn(List.of(Category.builder()
                .id(UUID.randomUUID()).name("Cat").active(true).build()));

        mvc.perform(get("/api/v1/catalog/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Cat"));
    }

    @Test
    void brands_ok() throws Exception {
        when(brandQueries.list(null)).thenReturn(List.of(Brand.builder()
                .id(UUID.randomUUID()).name("Brand").active(true).build()));

        mvc.perform(get("/api/v1/catalog/brands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Brand"));
    }

    @Test
    void products_ok() throws Exception {
        var p = Product.builder()
                .id(UUID.randomUUID()).name("Prod").shortDesc("d")
                .brandId(UUID.randomUUID()).categoryId(UUID.randomUUID())
                .published(true)
                .createdAt(Instant.now()).build();
        when(productQueries.search(null, null, null, 0, 20))
                .thenReturn(new PageResult<>(List.of(p),0,20,1,1));

        mvc.perform(get("/api/v1/catalog/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Prod"));
    }

    @Test
    void productDetail_ok() throws Exception {
        var p = Product.builder()
                .id(UUID.randomUUID()).name("Prod").shortDesc("d")
                .brandId(UUID.randomUUID()).categoryId(UUID.randomUUID())
                .published(true)
                .createdAt(Instant.now()).updatedAt(Instant.now()).build();
        when(productQueries.getById(p.getId())).thenReturn(p);

        mvc.perform(get("/api/v1/catalog/products/"+p.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Prod"));
    }
}

