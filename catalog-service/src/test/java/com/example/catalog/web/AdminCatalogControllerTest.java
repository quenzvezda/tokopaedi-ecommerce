package com.example.catalog.web;

import com.example.catalog.application.brand.BrandCommands;
import com.example.catalog.application.category.CategoryCommands;
import com.example.catalog.application.product.ProductCommands;
import com.example.catalog.application.sku.SkuCommands;
import com.example.catalog.domain.brand.Brand;
import com.example.catalog.domain.category.Category;
import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.sku.Sku;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminCatalogController.class)
@Import({GlobalExceptionHandler.class, WebTestConfig.class})
class AdminCatalogControllerTest {

    @Autowired MockMvc mvc;

    @MockBean CategoryCommands categoryCommands;
    @MockBean BrandCommands brandCommands;
    @MockBean ProductCommands productCommands;
    @MockBean SkuCommands skuCommands;

    private final ObjectMapper om = new ObjectMapper();

    @Test
    void createCategory_ok() throws Exception {
        var c = Category.builder().id(UUID.randomUUID()).name("C").active(true).build();
        when(categoryCommands.create(any(), any(), any(), any())).thenReturn(c);

        mvc.perform(post("/api/v1/admin/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(Map.of("name","C"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("C"));
    }

    @Test
    void updateCategory_ok() throws Exception {
        UUID id = UUID.randomUUID();
        var c = Category.builder().id(id).name("CC").active(true).build();
        when(categoryCommands.update(eq(id), any(), any(), any(), any())).thenReturn(c);

        mvc.perform(put("/api/v1/admin/categories/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(Map.of("name","CC"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("CC"));
    }

    @Test
    void deleteCategory_ok() throws Exception {
        UUID id = UUID.randomUUID();
        mvc.perform(delete("/api/v1/admin/categories/"+id))
                .andExpect(status().isNoContent());
        verify(categoryCommands).delete(id);
    }

    @Test
    void createBrand_ok() throws Exception {
        var b = Brand.builder().id(UUID.randomUUID()).name("B").active(true).build();
        when(brandCommands.create(any(), any())).thenReturn(b);

        mvc.perform(post("/api/v1/admin/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(Map.of("name","B"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("B"));
    }

    @Test
    void updateBrand_ok() throws Exception {
        UUID id = UUID.randomUUID();
        var b = Brand.builder().id(id).name("BB").active(false).build();
        when(brandCommands.update(eq(id), any(), any())).thenReturn(b);

        mvc.perform(put("/api/v1/admin/brands/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(Map.of("name","BB","active",false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("BB"))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void deleteBrand_ok() throws Exception {
        UUID id = UUID.randomUUID();
        mvc.perform(delete("/api/v1/admin/brands/"+id))
                .andExpect(status().isNoContent());
        verify(brandCommands).delete(id);
    }

    @Test
    void createProduct_ok() throws Exception {
        UUID brandId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        var p = Product.builder().id(UUID.randomUUID()).name("P").shortDesc("d")
                .brandId(brandId).categoryId(categoryId)
                .published(true).createdAt(Instant.now()).updatedAt(Instant.now()).build();
        when(productCommands.create(any(), any(), any(), any(), any())).thenReturn(p);

        mvc.perform(post("/api/v1/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(Map.of(
                        "name","P",
                        "shortDesc","d",
                        "brandId", brandId,
                        "categoryId", categoryId
                ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("P"));
    }

    @Test
    void updateProduct_ok() throws Exception {
        UUID id = UUID.randomUUID();
        var p = Product.builder().id(id).name("PP").shortDesc("d")
                .brandId(UUID.randomUUID()).categoryId(UUID.randomUUID())
                .published(true).createdAt(Instant.now()).updatedAt(Instant.now()).build();
        when(productCommands.update(eq(id), any(), any(), any(), any(), any())).thenReturn(p);

        mvc.perform(put("/api/v1/admin/products/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(Map.of("name","PP"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("PP"));
    }

    @Test
    void deleteProduct_ok() throws Exception {
        UUID id = UUID.randomUUID();
        mvc.perform(delete("/api/v1/admin/products/"+id))
                .andExpect(status().isNoContent());
        verify(productCommands).delete(id);
    }

    @Test
    void createSku_ok() throws Exception {
        UUID pid = UUID.randomUUID();
        var s = Sku.builder().id(UUID.randomUUID()).productId(pid).skuCode("S").active(true).build();
        when(skuCommands.create(eq(pid), any(), any(), any())).thenReturn(s);

        mvc.perform(post("/api/v1/admin/products/"+pid+"/skus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(Map.of(
                        "productId", pid,
                        "skuCode","S"
                ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.skuCode").value("S"));
    }

    @Test
    void updateSku_ok() throws Exception {
        UUID id = UUID.randomUUID();
        var s = Sku.builder().id(id).productId(UUID.randomUUID()).skuCode("SS").active(true).build();
        when(skuCommands.update(eq(id), any(), any(), any())).thenReturn(s);

        mvc.perform(put("/api/v1/admin/skus/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(Map.of("skuCode","SS"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skuCode").value("SS"));
    }

    @Test
    void deleteSku_ok() throws Exception {
        UUID id = UUID.randomUUID();
        mvc.perform(delete("/api/v1/admin/skus/"+id))
                .andExpect(status().isNoContent());
        verify(skuCommands).delete(id);
    }
}

