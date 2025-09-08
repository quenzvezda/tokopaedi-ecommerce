package com.example.iam.web;

import com.example.iam.application.role.RoleCommands;
import com.example.iam.application.role.RoleQueries;
import com.example.iam.domain.role.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RoleController.class)
@Import({GlobalExceptionHandler.class, WebTestConfig.class})
class RoleControllerTest {

    @Autowired
    private MockMvc mvc;
    private final ObjectMapper om = new ObjectMapper();

    @MockBean RoleCommands commands;
    @MockBean RoleQueries queries;

    @Test
    void list_ok() throws Exception {
        when(queries.list()).thenReturn(List.of(new Role(1L,"ADMIN")));
        mvc.perform(get("/iam/api/v1/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("ADMIN"));
    }

    @Test
    void get_found_ok() throws Exception {
        when(queries.getById(1L)).thenReturn(new Role(1L,"ADMIN"));
        mvc.perform(get("/iam/api/v1/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ADMIN"));
    }

    @Test
    void get_missing_404() throws Exception {
        when(queries.getById(anyLong())).thenThrow(new NoSuchElementException());
        mvc.perform(get("/iam/api/v1/roles/9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("not_found"));
    }

    @Test
    void create_valid_ok() throws Exception {
        when(commands.create("USER")).thenReturn(new Role(10L,"USER"));
        mvc.perform(post("/iam/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(Map.of("name","USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void create_malformedJson_400() throws Exception {
        mvc.perform(post("/iam/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad_request:malformed_json"));
    }

    @Test
    void update_ok() throws Exception {
        when(commands.update(1L,"MANAGER")).thenReturn(new Role(1L,"MANAGER"));
        mvc.perform(put("/iam/api/v1/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(Map.of("name","MANAGER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("MANAGER"));
    }

    @Test
    void delete_ok() throws Exception {
        mvc.perform(delete("/iam/api/v1/roles/1"))
                .andExpect(status().isNoContent());
        verify(commands).delete(1L);
    }
}
