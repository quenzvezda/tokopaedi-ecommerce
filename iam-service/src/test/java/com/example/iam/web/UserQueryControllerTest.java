package com.example.iam.web;

import com.example.iam.application.user.UserQueries;
import com.example.iam.application.entitlement.EntitlementQueries;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import com.example.common.web.response.ErrorProps;
import com.example.common.web.response.ErrorResponseBuilder;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@Import({GlobalExceptionHandler.class, UserQueryControllerTest.TestConfig.class})
class UserQueryControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        ErrorProps errorProps() {
            var p = new ErrorProps();
            p.setVerbose(false);
            return p;
        }

        @Bean
        ErrorResponseBuilder errorResponseBuilder(ErrorProps props) {
            return new ErrorResponseBuilder(props, "iam-service");
        }

        @Bean
        SecurityFilterChain testSecurity(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/iam/internal/v1/**").hasRole("ADMIN")
                            .requestMatchers("/iam/api/v1/users/*/roles").hasRole("ADMIN")
                            .anyRequest().denyAll());
            return http.build();
        }
    }

    @Autowired
    private MockMvc mvc;
    @MockBean UserQueries queries;
    @MockBean EntitlementQueries entitlements;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRoles_ok() throws Exception {
        var acc = UUID.randomUUID();
        when(queries.getUserRoleNames(acc)).thenReturn(List.of("ADMIN","USER"));

        mvc.perform(get("/iam/internal/v1/users/" + acc + "/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("ADMIN"))
                .andExpect(jsonPath("$[1]").value("USER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRolesPublic_ok() throws Exception {
        var acc = UUID.randomUUID();
        when(queries.getUserRoleNames(acc)).thenReturn(List.of("ADMIN","USER"));

        mvc.perform(get("/iam/api/v1/users/" + acc + "/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("ADMIN"))
                .andExpect(jsonPath("$[1]").value("USER"));
    }

    @Test
    @WithMockUser // default role USER
    void getRolesPublic_forbidden() throws Exception {
        var acc = UUID.randomUUID();

        mvc.perform(get("/iam/api/v1/users/" + acc + "/roles"))
                .andExpect(status().isForbidden());
    }
}
