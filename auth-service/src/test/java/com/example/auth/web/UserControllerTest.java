package com.example.auth.web;

import com.example.auth.application.account.AccountQueries;
import com.example.auth.config.CommonWebConfig;
import com.example.auth.domain.account.Account;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CommonWebConfig.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Resource MockMvc mvc;
    @MockBean AccountQueries accountQueries;

    @Test
    void listUsers_ok() throws Exception {
        var a1 = Account.of(UUID.randomUUID(), "alice", "a@x.io", "h", "ACTIVE", OffsetDateTime.now(ZoneOffset.UTC));
        var a2 = Account.of(UUID.randomUUID(), "bob", "b@x.io", "h", "ACTIVE", OffsetDateTime.now(ZoneOffset.UTC));
        when(accountQueries.list()).thenReturn(List.of(a1, a2));

        mvc.perform(get("/auth/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(a1.getId().toString()))
                .andExpect(jsonPath("$[0].username").value("alice"))
                .andExpect(jsonPath("$[1].id").value(a2.getId().toString()))
                .andExpect(jsonPath("$[1].username").value("bob"));
    }
}
