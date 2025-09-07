package com.example.catalog.web.brand;

import com.example.catalog.application.brand.BrandCommands;
import com.example.catalog.application.brand.BrandQueries;
import com.example.catalog.domain.brand.Brand;
import com.example.catalog.web.dto.BrandCreateRequest;
import com.example.catalog.web.dto.BrandUpdateRequest;
import com.example.catalog.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import com.example.catalog.web.WebTestConfig;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BrandController.class)
@Import({GlobalExceptionHandler.class, WebTestConfig.class})
class BrandControllerTest {

    @Autowired MockMvc mvc;
    @MockBean BrandQueries brandQueries;
    @MockBean BrandCommands brandCommands;

    @Test
    void list_ok() throws Exception {
        when(brandQueries.list(null)).thenReturn(List.of(Brand.builder().id(UUID.randomUUID()).name("B").active(true).build()));
        mvc.perform(get("/api/v1/brands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("B"));
    }

    @Test
    void create_ok() throws Exception {
        var b = Brand.builder().id(UUID.randomUUID()).name("B").active(true).build();
        when(brandCommands.create(any(), any())).thenReturn(b);
        var om = new com.fasterxml.jackson.databind.ObjectMapper();
        mvc.perform(post("/api/v1/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(new BrandCreateRequest("B", true))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("B"));
    }

    @Test
    void create_missingName_400() throws Exception {
        var om = new com.fasterxml.jackson.databind.ObjectMapper();
        // omit name to violate @NotNull on generated request model
        mvc.perform(post("/api/v1/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(java.util.Map.of("active", true))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad_request:validation"));
    }

    @Test
    void update_ok() throws Exception {
        UUID id = UUID.randomUUID();
        var b = Brand.builder().id(id).name("BB").active(false).build();
        when(brandCommands.update(eq(id), any(), any())).thenReturn(b);
        var om = new com.fasterxml.jackson.databind.ObjectMapper();
        mvc.perform(put("/api/v1/brands/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(new BrandUpdateRequest("BB", false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("BB"))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void delete_ok() throws Exception {
        UUID id = UUID.randomUUID();
        mvc.perform(delete("/api/v1/brands/"+id))
                .andExpect(status().isNoContent());
        verify(brandCommands).delete(id);
    }
}
