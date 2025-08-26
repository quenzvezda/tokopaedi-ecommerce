package com.example.auth.web;

import com.example.auth.application.account.AccountCommands;
import com.example.auth.application.auth.AuthCommands.TokenPair;
import com.example.auth.application.auth.AuthCommands;
import com.example.auth.config.CommonWebConfig;
import com.example.auth.web.dto.LoginRequest;
import com.example.auth.web.dto.RegisterRequest;
import com.example.auth.web.error.UsernameAlreadyExistsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.annotation.Resource;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security filter chain
@Import(CommonWebConfig.class)            // supaya ErrorResponseBuilder ter-load
@ActiveProfiles("test")
class AuthControllerTest {

	@Resource MockMvc mvc;
	@Resource ObjectMapper om;

	@MockBean AuthCommands authCommands;
	@MockBean AccountCommands accountCommands;

	@Test
	void register_validationError_returns400WithUpstream() throws Exception {
		var req = new RegisterRequest();
		req.setUsername("ab"); // invalid (min 3)
		req.setEmail("bad");
		req.setPassword("123");

		mvc.perform(post("/api/v1/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(om.writeValueAsString(req)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("bad_request:validation"))
				.andExpect(jsonPath("$.upstream.errors", hasSize(greaterThan(0))));
	}

	@Test
	void register_conflictFromService_returns409() throws Exception {
		var req = new RegisterRequest();
		req.setUsername("alice");
		req.setEmail("a@x.io");
		req.setPassword("secret");

		when(accountCommands.register("alice","a@x.io","secret"))
				.thenThrow(new UsernameAlreadyExistsException());

		mvc.perform(post("/api/v1/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(om.writeValueAsString(req)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("username_taken"));
	}

	@Test
	void login_success_returnsTokenPair() throws Exception {
		var body = new LoginRequest();
		body.setUsernameOrEmail("alice");
		body.setPassword("secret");

		when(authCommands.login("alice","secret"))
				.thenReturn(new TokenPair("Bearer","jwt",900,"rt"));

		mvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(om.writeValueAsString(body)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.accessToken").value("jwt"));
	}
}
