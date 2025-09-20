package com.example.auth.web;

import com.example.auth.application.account.AccountQueries;
import com.example.auth.config.CommonWebConfig;
import com.example.auth.config.SecurityConfig;
import com.example.auth.domain.account.Account;
import com.example.auth.domain.common.PageResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
@Import({CommonWebConfig.class, SecurityConfig.class})
@ActiveProfiles("test")
class UserControllerTest {

    @Resource MockMvc mvc;
    @MockBean AccountQueries accountQueries;
    @MockBean JwtDecoder jwtDecoder;

    @Test
    void listUsers_unauthenticated_returns401() throws Exception {
        mvc.perform(get("/auth/api/v1/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listUsers_ok() throws Exception {
        var a1 = Account.of(UUID.randomUUID(), "alice", "a@x.io", "h", "ACTIVE", OffsetDateTime.now(ZoneOffset.UTC));
        var a2 = Account.of(UUID.randomUUID(), "bob", "b@x.io", "h", "ACTIVE", OffsetDateTime.now(ZoneOffset.UTC));
        when(accountQueries.search(null, null, 0, 20))
                .thenReturn(PageResult.of(List.of(a1, a2), 0, 20, 2));

        mvc.perform(get("/auth/api/v1/users").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.content[0].id").value(a1.getId().toString()))
                .andExpect(jsonPath("$.content[0].username").value("alice"))
                .andExpect(jsonPath("$.content[1].id").value(a2.getId().toString()))
                .andExpect(jsonPath("$.content[1].username").value("bob"));

        verify(accountQueries).search(null, null, 0, 20);
    }

    @Test
    void listUsers_withQueryParams_forwardsArguments() throws Exception {
        when(accountQueries.search("alice", List.of("username,desc"), 2, 50))
                .thenReturn(PageResult.of(List.of(), 2, 50, 0));

        mvc.perform(get("/auth/api/v1/users?page=2&size=50&q=alice&sort=username,desc").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.number").value(2))
                .andExpect(jsonPath("$.size").value(50));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> sortCaptor = ArgumentCaptor.forClass(List.class);
        verify(accountQueries).search(org.mockito.Mockito.eq("alice"), sortCaptor.capture(), org.mockito.Mockito.eq(2), org.mockito.Mockito.eq(50));
        assertThat(sortCaptor.getValue()).containsExactly("username,desc");
    }
}
