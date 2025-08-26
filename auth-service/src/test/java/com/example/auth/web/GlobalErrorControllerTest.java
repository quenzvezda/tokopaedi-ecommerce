package com.example.auth.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

	@Test
	void unknownEndpoint_returns404() throws Exception {
		mvc.perform(post("/api/v1/auth/registered")) // tidak ada handler
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code", equalTo("not_found")))
				.andExpect(jsonPath("$.message", equalTo("Resource not found")));
	}
}
