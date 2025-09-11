package com.example.iam.web;

import com.example.iam.application.role.RoleCommands;
import com.example.iam.application.role.RoleQueries;
import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.role.Role;
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

@WebMvcTest(controllers = RoleController.class)
@Import({GlobalExceptionHandler.class, WebTestConfig.class})
class RoleControllerTest {

    @Autowired
    private MockMvc mvc;
    private final ObjectMapper om = new ObjectMapper();

    @MockBean RoleCommands commands;
    @MockBean RoleQueries queries;

    @Test
    void list_v1_ok() throws Exception {
        when(queries.list(0, Integer.MAX_VALUE)).thenReturn(PageResult.of(List.of(new Role(1L,"ADMIN")), 0, Integer.MAX_VALUE, 1));
        mvc.perform(get("/iam/api/v1/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("ADMIN"));
    }

    @Test
    void list_v2_ok() throws Exception {
        when(queries.list(0, 20)).thenReturn(PageResult.of(List.of(new Role(1L,"ADMIN")), 0, 20, 1));
        mvc.perform(get("/iam/api/v2/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("ADMIN"))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
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

    @Test
    void list_permissions_v1_ok() throws Exception {
        when(queries.listPermissions(1L, 0, Integer.MAX_VALUE)).thenReturn(PageResult.of(List.of(new Permission(1L, "READ", null)), 0, Integer.MAX_VALUE, 1));
        mvc.perform(get("/iam/api/v1/roles/1/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("READ"));
    }

    @Test
    void list_permissions_v2_ok() throws Exception {
        when(queries.listPermissions(1L, 0, 20)).thenReturn(PageResult.of(List.of(new Permission(1L, "READ", null)), 0, 20, 1));
        mvc.perform(get("/iam/api/v2/roles/1/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("READ"));
    }

    @Test
    void list_available_permissions_v1_ok() throws Exception {
        when(queries.listAvailablePermissions(1L, 0, Integer.MAX_VALUE)).thenReturn(PageResult.of(List.of(new Permission(2L, "WRITE", null)), 0, Integer.MAX_VALUE, 1));
        mvc.perform(get("/iam/api/v1/roles/1/permissions/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("WRITE"));
    }

    @Test
    void list_available_permissions_v2_ok() throws Exception {
        when(queries.listAvailablePermissions(1L, 0, 20)).thenReturn(PageResult.of(List.of(new Permission(2L, "WRITE", null)), 0, 20, 1));
        mvc.perform(get("/iam/api/v2/roles/1/permissions/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("WRITE"));
    }
}
