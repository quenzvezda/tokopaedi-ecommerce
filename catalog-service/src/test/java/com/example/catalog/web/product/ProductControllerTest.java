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
                .published(true).createdAt(Instant.now()).build();
        when(productQueries.search(null,null,null,0,20)).thenReturn(new PageResult<>(List.of(p),0,20,1,1));
        mvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].slug").value("prod"));
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
