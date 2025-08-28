package com.example.iam.web;

import com.example.iam.application.entitlement.EntitlementQueries;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthzController.class)
@Import({GlobalExceptionHandler.class, WebTestConfig.class})
class AuthzControllerTest {

    @Autowired
    private MockMvc mvc;
    private final ObjectMapper om = new ObjectMapper();
    @MockBean EntitlementQueries queries;

    @Test
    void check_valid_returnsDecision() throws Exception {
        var sub = UUID.randomUUID();
        when(queries.checkAuthorization(sub, "READ")).thenReturn(Map.of("decision","ALLOW","ent_v",2));

        var body = Map.of("sub", sub.toString(), "action", "READ");

        mvc.perform(post("/api/v1/authz/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decision").value("ALLOW"))
                .andExpect(jsonPath("$.ent_v").value(2));
    }

    @Test
    void check_invalidBody_triggersValidationError() throws Exception {
        var bad = Map.of("sub", "", "action", " "); // @NotNull + @NotBlank fail

        mvc.perform(post("/api/v1/authz/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad_request:validation"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void check_malformedJson_returns400() throws Exception {
        mvc.perform(post("/api/v1/authz/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad_request:malformed_json"));
    }
}
