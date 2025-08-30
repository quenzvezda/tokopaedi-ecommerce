package com.example.catalog.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PingController.class)
@Import({GlobalExceptionHandler.class, WebTestConfig.class})
class PingControllerTest {

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper om = new ObjectMapper();

    @Test
    void ping_ok() throws Exception {
        mvc.perform(get("/api/v1/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("pong"));
    }

    @Test
    void secure_ok() throws Exception {
        mvc.perform(get("/api/v1/secure"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("secured"));
    }

    @Test
    void echo_ok() throws Exception {
        mvc.perform(post("/api/v1/echo").param("text", "hi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.echo").value("hi"));
    }

    @Test
    void echo_blank_400() throws Exception {
        mvc.perform(post("/api/v1/echo").param("text", " "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad_request:validation"));
    }
}

