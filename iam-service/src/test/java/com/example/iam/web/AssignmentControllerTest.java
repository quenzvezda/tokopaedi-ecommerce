package com.example.iam.web;

import com.example.iam.application.assignment.AssignmentCommands;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AssignmentController.class)
@Import({GlobalExceptionHandler.class, WebTestConfig.class})
class AssignmentControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean AssignmentCommands commands;

    @Test
    void addPermissionToRole_ok() throws Exception {
        mvc.perform(post("/api/v1/assign/role/1/permission/10"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        verify(commands).assignPermissionToRole(1L, 10L);
    }

    @Test
    void removePermissionFromRole_ok() throws Exception {
        mvc.perform(delete("/api/v1/assign/role/2/permission/9"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        verify(commands).removePermissionFromRole(2L, 9L);
    }

    @Test
    void addRoleToUser_ok() throws Exception {
        var acc = UUID.randomUUID();
        mvc.perform(post("/api/v1/assign/user/" + acc + "/role/3"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        verify(commands).assignRoleToUser(acc, 3L);
    }

    @Test
    void removeRoleFromUser_ok() throws Exception {
        var acc = UUID.randomUUID();
        mvc.perform(delete("/api/v1/assign/user/" + acc + "/role/5"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        verify(commands).removeRoleFromUser(acc, 5L);
    }
}
