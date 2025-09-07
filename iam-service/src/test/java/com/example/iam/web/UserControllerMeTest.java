package com.example.iam.web;

import com.example.iam.application.entitlement.EntitlementQueries;
import com.example.iam.application.user.UserQueries;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import({GlobalExceptionHandler.class, WebTestConfig.class})
class UserControllerMeTest {

    @Autowired
    private MockMvc mvc;

    @MockBean UserQueries userQueries;
    @MockBean EntitlementQueries entitlements;

    @Test
    void me_ok_withJwt() throws Exception {
        UUID sub = UUID.randomUUID();
        when(entitlements.getEntitlements(any())).thenReturn(Map.of("scopes", List.of("catalog:product:read", "catalog:product:write")));

        var jwt = SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j
                .subject(sub.toString())
                .claim("username", "john")
                .claim("email", "john@example.com")
                .claim("roles", List.of("ADMIN", "USER"))
        );

        mvc.perform(get("/api/v1/users/me").with(jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sub.toString()))
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"))
                .andExpect(jsonPath("$.permissions[0]").value("catalog:product:read"));
    }

    @Test
    void me_unauthorized_withoutJwt() throws Exception {
        mvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }
}

