package com.example.auth.web;

import com.example.auth.config.CommonWebConfig;
import com.example.auth.web.support.DummyErrorController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.annotation.Resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DummyErrorController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security filter
@Import(CommonWebConfig.class)            // ErrorResponseBuilder dari common-web
@ActiveProfiles("test")
class GlobalExceptionAdviceBranchesTest {

    @Resource MockMvc mvc;

    @Test
    void constraintViolation_returns400_withUpstreamErrors() throws Exception {
        mvc.perform(get("/t/constraint").param("n", "1")) // n<5 picu @Min
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad_request:constraints"))
                .andExpect(jsonPath("$.upstream.errors").isArray());
    }

    @Test
    void dataConflict_returns409() throws Exception {
        mvc.perform(get("/t/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("data_conflict"));
    }

    @Test
    void illegalArgument_returns400() throws Exception {
        mvc.perform(get("/t/illegal"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad_request"));
    }

    @Test
    void accessDenied_returns403() throws Exception {
        mvc.perform(get("/t/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("forbidden"));
    }

    @Test
    void springError_wrappedProperly() throws Exception {
        mvc.perform(get("/t/spring"))
                .andExpect(status().isIAmATeapot())
                .andExpect(jsonPath("$.code").value("spring_error"));
    }
}
