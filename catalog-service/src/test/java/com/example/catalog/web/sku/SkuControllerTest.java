package com.example.catalog.web.sku;

import com.example.catalog.application.sku.SkuCommands;
import com.example.catalog.domain.sku.Sku;
import com.example.catalog.web.dto.SkuCreateRequest;
import com.example.catalog.web.dto.SkuUpdateRequest;
import com.example.catalog.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import com.example.catalog.web.WebTestConfig;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SkuController.class)
@Import({GlobalExceptionHandler.class, WebTestConfig.class})
class SkuControllerTest {

    @Autowired MockMvc mvc;
    @MockBean SkuCommands skuCommands;

    @Test
    void create_ok() throws Exception {
        UUID pid = UUID.randomUUID();
        var s = Sku.builder().id(UUID.randomUUID()).productId(pid).skuCode("S").active(true).build();
        when(skuCommands.create(eq(pid), any(), any(), any())).thenReturn(s);
        var om = new com.fasterxml.jackson.databind.ObjectMapper();
        mvc.perform(post("/api/v1/admin/products/"+pid+"/skus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(new SkuCreateRequest(pid, "S", true, null))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.skuCode").value("S"));
    }

    @Test
    void update_ok() throws Exception {
        UUID id = UUID.randomUUID();
        var s = Sku.builder().id(id).productId(UUID.randomUUID()).skuCode("SS").active(true).build();
        when(skuCommands.update(eq(id), any(), any(), any())).thenReturn(s);
        var om = new com.fasterxml.jackson.databind.ObjectMapper();
        mvc.perform(put("/api/v1/admin/skus/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(new SkuUpdateRequest("SS", true, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skuCode").value("SS"));
    }

    @Test
    void delete_ok() throws Exception {
        UUID id = UUID.randomUUID();
        mvc.perform(delete("/api/v1/admin/skus/"+id))
                .andExpect(status().isNoContent());
        verify(skuCommands).delete(id);
    }
}
