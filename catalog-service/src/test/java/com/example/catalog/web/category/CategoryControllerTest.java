package com.example.catalog.web.category;

import com.example.catalog.application.category.CategoryCommands;
import com.example.catalog.application.category.CategoryQueries;
import com.example.catalog.domain.category.Category;
import com.example.catalog.web.dto.CategoryCreateRequest;
import com.example.catalog.web.dto.CategoryUpdateRequest;
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

@WebMvcTest(controllers = CategoryController.class)
@Import({GlobalExceptionHandler.class, WebTestConfig.class})
class CategoryControllerTest {

    @Autowired MockMvc mvc;
    @MockBean CategoryQueries categoryQueries;
    @MockBean CategoryCommands categoryCommands;

    @Test
    void list_ok() throws Exception {
        when(categoryQueries.list(null, null)).thenReturn(List.of(Category.builder().id(UUID.randomUUID()).name("C").active(true).build()));
        mvc.perform(get("/catalog/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("C"));
    }

    @Test
    void create_ok() throws Exception {
        var c = Category.builder().id(UUID.randomUUID()).name("C").active(true).build();
        when(categoryCommands.create(any(), any(), any(), any())).thenReturn(c);
        var om = new com.fasterxml.jackson.databind.ObjectMapper();
        mvc.perform(post("/catalog/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(new CategoryCreateRequest("C", null, true, null))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("C"));
    }

    @Test
    void update_ok() throws Exception {
        UUID id = UUID.randomUUID();
        var c = Category.builder().id(id).name("CC").active(false).build();
        when(categoryCommands.update(eq(id), any(), any(), any(), any())).thenReturn(c);
        var om = new com.fasterxml.jackson.databind.ObjectMapper();
        mvc.perform(put("/catalog/api/v1/categories/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(new CategoryUpdateRequest("CC", null, false, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("CC"))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void delete_ok() throws Exception {
        UUID id = UUID.randomUUID();
        mvc.perform(delete("/catalog/api/v1/categories/"+id))
                .andExpect(status().isNoContent());
        verify(categoryCommands).delete(id);
    }
}
