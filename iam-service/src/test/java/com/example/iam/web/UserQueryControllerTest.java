package com.example.iam.web;

import com.example.iam.application.user.UserQueries;
import com.example.iam.application.entitlement.EntitlementQueries;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@Import({GlobalExceptionHandler.class, WebTestConfig.class})
class UserQueryControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean UserQueries queries;
    @MockBean EntitlementQueries entitlements;

    @Test
    void getRoles_ok() throws Exception {
        var acc = UUID.randomUUID();
        when(queries.getUserRoleNames(acc)).thenReturn(List.of("ADMIN","USER"));

        mvc.perform(get("/iam/internal/v1/users/" + acc + "/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("ADMIN"))
                .andExpect(jsonPath("$[1]").value("USER"));
    }
}
