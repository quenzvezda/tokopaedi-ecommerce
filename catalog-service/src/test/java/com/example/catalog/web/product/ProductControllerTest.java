package com.example.catalog.web.product;

import com.example.catalog.application.product.ProductCommands;
import com.example.catalog.application.product.ProductQueries;
import com.example.catalog.domain.common.PageResult;
import com.example.catalog.domain.product.Product;
import com.example.catalog.web.dto.ProductCreateRequest;
import com.example.catalog.web.dto.ProductUpdateRequest;
import com.example.catalog.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import com.example.catalog.web.WebTestConfig;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class)
@Import({GlobalExceptionHandler.class, WebTestConfig.class})
class ProductControllerTest {

    @Autowired MockMvc mvc;
    @MockBean ProductQueries productQueries;
    @MockBean ProductCommands productCommands;

    @Test
    void list_ok() throws Exception {
        var p = Product.builder().id(UUID.randomUUID()).name("Prod").slug("prod").shortDesc("d")
                .brandId(UUID.randomUUID()).categoryId(UUID.randomUUID())
                .published(true).createdAt(Instant.now()).updatedAt(Instant.now()).build();
        when(productQueries.search("shoe", null, null, 1, 2)).thenReturn(PageResult.of(List.of(p), 1, 2, 10));

        mvc.perform(get("/api/v1/products").param("q", "shoe").param("page", "1").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Prod"))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(10));
    }

    @Test
    void list_withBrandAndCategory_noPaging_usesDefaults() throws Exception {
        UUID brandId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        var p = Product.builder().id(UUID.randomUUID()).name("Filtered").slug("filtered").shortDesc("desc")
                .brandId(brandId).categoryId(categoryId)
                .published(true).createdAt(Instant.now()).updatedAt(Instant.now()).build();

        // Expect default paging page=0,size=20 when omitted
        when(productQueries.search("shirt", brandId, categoryId, 0, 20))
                .thenReturn(PageResult.of(List.of(p), 0, 20, 1));

        mvc.perform(get("/api/v1/products")
                        .param("q", "shirt")
                        .param("brandId", brandId.toString())
                        .param("categoryId", categoryId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.content[0].name").value("Filtered"))
                .andExpect(jsonPath("$.content[0].description").value("desc"));
    }

    @Test
    void detail_ok() throws Exception {
        var p = Product.builder().id(UUID.randomUUID()).name("Prod").slug("prod").shortDesc("d")
                .brandId(UUID.randomUUID()).categoryId(UUID.randomUUID())
                .published(true).createdAt(Instant.now()).updatedAt(Instant.now()).build();
        when(productQueries.getBySlug("prod")).thenReturn(p);
        mvc.perform(get("/api/v1/products/prod"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("prod"));
    }

    @Test
    void create_ok() throws Exception {
        UUID brandId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        var p = Product.builder().id(UUID.randomUUID()).name("P").slug("p").shortDesc("d")
                .brandId(brandId).categoryId(categoryId)
                .published(true).createdAt(Instant.now()).updatedAt(Instant.now()).build();
        when(productCommands.create(any(), any(), any(), any(), any())).thenReturn(p);
        var om = new com.fasterxml.jackson.databind.ObjectMapper();
        mvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(new ProductCreateRequest("P", "d", brandId, categoryId, true))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("p"));
    }

    @Test
    void detail_notFound_404() throws Exception {
        when(productQueries.getBySlug("missing"))
                .thenThrow(new java.util.NoSuchElementException("not found"));

        mvc.perform(get("/api/v1/products/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("not_found"));
    }

    @Test
    void create_missingBrandId_400() throws Exception {
        UUID categoryId = UUID.randomUUID();
        var om = new com.fasterxml.jackson.databind.ObjectMapper();
        // brandId is null -> violates @NotNull on generated model
        mvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(new ProductCreateRequest("P", "d", null, categoryId, true))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad_request:validation"));
    }

    @Test
    void update_ok() throws Exception {
        UUID id = UUID.randomUUID();
        var p = Product.builder().id(id).name("PP").slug("pp").shortDesc("d")
                .brandId(UUID.randomUUID()).categoryId(UUID.randomUUID())
                .published(true).createdAt(Instant.now()).updatedAt(Instant.now()).build();
        when(productCommands.update(eq(id), any(), any(), any(), any(), any())).thenReturn(p);
        var om = new com.fasterxml.jackson.databind.ObjectMapper();
        mvc.perform(put("/api/v1/products/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(new ProductUpdateRequest("PP", "d", null, null, true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("pp"));
    }

    @Test
    void delete_ok() throws Exception {
        UUID id = UUID.randomUUID();
        mvc.perform(delete("/api/v1/products/"+id))
                .andExpect(status().isNoContent());
        verify(productCommands).delete(id);
    }
}
