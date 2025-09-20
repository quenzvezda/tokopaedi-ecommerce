package com.example.iam.web;

import com.example.iam.application.permission.PermissionCommands;
import com.example.iam.application.permission.PermissionCommands.CreatePermission;
import com.example.iam.application.permission.PermissionQueries;
import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.common.PageResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
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
        when(queries.search(null, null, 0, 20)).thenReturn(PageResult.of(List.of(new Permission(1L,"A","d")), 0, 20, 1));
        mvc.perform(get("/iam/api/v2/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("A"))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void list_v2_with_q_and_sort_ok() throws Exception {
        when(queries.search("ord", List.of("name,asc","id,desc"), 1, 10))
                .thenReturn(PageResult.of(List.of(new Permission(2L,"ORDER_READ","d")), 1, 10, 11));
        mvc.perform(get("/iam/api/v2/permissions")
                        .param("q","ord")
                        .param("page","1")
                        .param("size","10")
                        .param("sort","name,asc")
                        .param("sort","id,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(11))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void list_v2_invalid_sort_400() throws Exception {
        // controller passes through to queries; invalid sort is validated in infra layer, but we simulate thrown IllegalArgumentException
        when(queries.search(any(), any(), anyInt(), anyInt())).thenThrow(new IllegalArgumentException("invalid sort field: bogus"));
        mvc.perform(get("/iam/api/v2/permissions").param("sort","bogus,asc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad_request:invalid_argument"));
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
    void create_bulk_ok() throws Exception {
        when(commands.createBulk(any())).thenReturn(List.of(new Permission(10L,"A","x"), new Permission(11L,"B","y")));
        mvc.perform(post("/iam/api/v2/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(Map.of(
                                "permissions",
                                List.of(
                                        Map.of("name","A","description","x"),
                                        Map.of("name","B","description","y")
                                )
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.created[0].id").value(10))
                .andExpect(jsonPath("$.created[1].name").value("B"));
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<CreatePermission>> captor = ArgumentCaptor.forClass(List.class);
        verify(commands).createBulk(captor.capture());
        assertThat(captor.getValue()).extracting(CreatePermission::name).containsExactly("A","B");
    }

    @Test
    void create_bulk_illegalArgument_400() throws Exception {
        when(commands.createBulk(any())).thenThrow(new IllegalArgumentException("duplicate"));
        mvc.perform(post("/iam/api/v2/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(Map.of(
                                "permissions",
                                List.of(Map.of("name","A","description","x"))
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad_request:invalid_argument"));
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

