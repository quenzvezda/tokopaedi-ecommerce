package com.example.auth.web;

import com.example.auth.application.jwk.JwkQueries;
import com.example.auth.config.CommonWebConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.annotation.Resource;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = JwksController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CommonWebConfig.class)
@ActiveProfiles("test")
class JwksControllerTest {

	@Resource MockMvc mvc;
	@MockBean JwkQueries q;

	@Test
	void jwks_ok() throws Exception {
		when(q.jwks()).thenReturn(Map.of("keys","x"));
		mvc.perform(get("/.well-known/jwks.json").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.keys").value("x"));
	}
}
