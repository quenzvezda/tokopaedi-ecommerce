package com.example.catalog.web.category;

import com.example.catalog.application.category.CategoryCommands;
import com.example.catalog.application.category.CategoryQueries;
import com.example.catalog.domain.category.Category;
import com.example.catalog.web.dto.CategoryCreateRequest;
import com.example.catalog.web.dto.CategoryUpdateRequest;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryController.class)
@Import({GlobalExceptionHandler.class, WebTestConfig.class})
class CategoryControllerTest {

    @Autowired MockMvc mvc;
    @MockBean CategoryQueries categoryQueries;
    @MockBean CategoryCommands categoryCommands;

    @Test
    void list_ok() throws Exception {
        when(categoryQueries.list(null, null)).thenReturn(List.of(Category.builder().id(UUID.randomUUID()).name("Cat").active(true).sortOrder(1).build()));
        mvc.perform(get("/catalog/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Cat"));
    }

    @Test
    void create_ok() throws Exception {
        var category = Category.builder().id(UUID.randomUUID()).name("Cat").active(true).build();
        when(categoryCommands.create(any(), any(), any(), any())).thenReturn(category);
        var om = new com.fasterxml.jackson.databind.ObjectMapper();
        mvc.perform(post("/catalog/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("catalog:category:write")))
                .content(om.writeValueAsBytes(new CategoryCreateRequest("Cat", null, true, 1))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cat"));
    }

    @Test
    void update_ok() throws Exception {
        UUID id = UUID.randomUUID();
        var category = Category.builder().id(id).name("Cat2").active(false).sortOrder(2).build();
        when(categoryCommands.update(eq(id), any(), any(), any(), any())).thenReturn(category);
        var om = new com.fasterxml.jackson.databind.ObjectMapper();
        mvc.perform(put("/catalog/api/v1/categories/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .with(jwt().authorities(new SimpleGrantedAuthority("catalog:category:write")))
                .content(om.writeValueAsBytes(new CategoryUpdateRequest("Cat2", null, false, 2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cat2"))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void delete_ok() throws Exception {
        UUID id = UUID.randomUUID();
        mvc.perform(delete("/catalog/api/v1/categories/" + id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("catalog:category:write"))))
                .andExpect(status().isNoContent());
        verify(categoryCommands).delete(id);
    }
}
