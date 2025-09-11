package com.example.iam.web;

import com.example.iam.application.permission.PermissionCommands;
import com.example.iam.application.permission.PermissionQueries;
import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.common.PageResult;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PermissionController.class)
@Import({GlobalExceptionHandler.class, WebTestConfig.class})
class PermissionControllerTest {

    @Autowired
    private MockMvc mvc;
    private final ObjectMapper om = new ObjectMapper();

    @MockBean PermissionCommands commands;
    @MockBean PermissionQueries queries;

    @Test
    void list_v1_ok() throws Exception {
        when(queries.list(0, Integer.MAX_VALUE)).thenReturn(PageResult.of(List.of(new Permission(1L,"A","d")), 0, Integer.MAX_VALUE, 1));
        mvc.perform(get("/iam/api/v1/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("A"));
    }

    @Test
    void list_v2_ok() throws Exception {
        when(queries.list(0, 20)).thenReturn(PageResult.of(List.of(new Permission(1L,"A","d")), 0, 20, 1));
        mvc.perform(get("/iam/api/v2/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("A"))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void get_found_ok() throws Exception {
        when(queries.getById(1L)).thenReturn(new Permission(1L,"A","d"));
        mvc.perform(get("/iam/api/v1/permissions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("A"));
    }

    @Test
    void get_missing_mappedTo404() throws Exception {
        when(queries.getById(anyLong())).thenThrow(new NoSuchElementException("not found"));
        mvc.perform(get("/iam/api/v1/permissions/9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("not_found"));
    }

    @Test
    void create_valid_ok() throws Exception {
        when(commands.create("A","x")).thenReturn(new Permission(10L,"A","x"));
        mvc.perform(post("/iam/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(Map.of("name","A","description","x"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void create_malformedJson_400() throws Exception {
        mvc.perform(post("/iam/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad_request:malformed_json"));
    }

    @Test
    void update_ok() throws Exception {
        when(commands.update(1L,"B","y")).thenReturn(new Permission(1L,"B","y"));
        mvc.perform(put("/iam/api/v1/permissions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(Map.of("name","B","description","y"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("B"));
    }

    @Test
    void delete_ok() throws Exception {
        mvc.perform(delete("/iam/api/v1/permissions/1"))
                .andExpect(status().isNoContent());
        verify(commands).delete(1L);
    }
}
