package com.example.catalog.web.sku;

import com.example.catalog.application.sku.SkuCommands;
import com.example.catalog.domain.sku.Sku;
import com.example.catalog.security.ProductAccessEvaluator;
import com.example.catalog.security.SkuAccessEvaluator;
import com.example.catalog.web.dto.SkuCreateRequest;
import com.example.catalog.web.dto.SkuUpdateRequest;
import com.example.catalog.web.GlobalExceptionHandler;
import com.example.catalog.web.WebTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SkuController.class)
@Import({GlobalExceptionHandler.class, WebTestConfig.class})
class SkuControllerTest {

    @Autowired MockMvc mvc;
    @MockBean SkuCommands skuCommands;
    @MockBean ProductAccessEvaluator productAccessEvaluator;
    @MockBean SkuAccessEvaluator skuAccessEvaluator;

    @Test
    void create_ok() throws Exception {
        UUID pid = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        var s = Sku.builder().id(UUID.randomUUID()).productId(pid).skuCode("S").active(true).build();
        when(productAccessEvaluator.requireCurrentActorId(any())).thenReturn(actorId);
        when(skuCommands.create(eq(actorId), eq(pid), any(), any(), any(), eq(true))).thenReturn(s);
        var om = new com.fasterxml.jackson.databind.ObjectMapper();
        mvc.perform(post("/catalog/api/v1/products/" + pid + "/skus")
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("catalog:sku:write")).jwt(jwt -> jwt.subject(actorId.toString())))
                .content(om.writeValueAsBytes(new SkuCreateRequest(pid, "S", true, null))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.skuCode").value("S"));
    }

    @Test
    void update_ok() throws Exception {
        UUID id = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        var s = Sku.builder().id(id).productId(UUID.randomUUID()).skuCode("SS").active(true).build();
        when(productAccessEvaluator.requireCurrentActorId(any())).thenReturn(actorId);
        when(skuCommands.update(eq(actorId), eq(id), any(), any(), any(), eq(true))).thenReturn(s);
        var om = new com.fasterxml.jackson.databind.ObjectMapper();
        mvc.perform(put("/catalog/api/v1/skus/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("catalog:sku:write")).jwt(jwt -> jwt.subject(actorId.toString())))
                .content(om.writeValueAsBytes(new SkuUpdateRequest("SS", true, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skuCode").value("SS"));
    }

    @Test
    void delete_ok() throws Exception {
        UUID id = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        when(productAccessEvaluator.requireCurrentActorId(any())).thenReturn(actorId);
        mvc.perform(delete("/catalog/api/v1/skus/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("catalog:sku:write")).jwt(jwt -> jwt.subject(actorId.toString()))))
                .andExpect(status().isNoContent());
        verify(skuCommands).delete(actorId, id, true);
    }
}
