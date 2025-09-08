package com.example.auth.web;

import com.example.auth.domain.entitlement.EntitlementClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class GlobalErrorControllerTest {

	@Autowired MockMvc mvc;

    @MockBean
    EntitlementClient entitlementClient;

	@Test
	void unknownEndpoint_returns404() throws Exception {
		mvc.perform(post("/auth/api/v1/registered")) // tidak ada handler
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code", equalTo("not_found")))
				.andExpect(jsonPath("$.message", equalTo("Resource not found")));
	}
}
