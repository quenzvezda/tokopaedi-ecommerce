package com.example.iam.web;

import com.example.iam.application.entitlement.EntitlementQueries;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EntitlementController.class)
@Import({GlobalExceptionHandler.class, WebTestConfig.class})
class EntitlementControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean EntitlementQueries queries;

    @Test
    void getEntitlements_ok() throws Exception {
        var acc = UUID.randomUUID();
        when(queries.getEntitlements(acc)).thenReturn(Map.of("perm_ver",3,"scopes", java.util.List.of("A")));

        mvc.perform(get("/internal/v1/entitlements/" + acc))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perm_ver").value(3))
                .andExpect(jsonPath("$.scopes[0]").value("A"));
    }
}
