package com.example.auth.web;

import com.example.auth.config.CommonWebConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.annotation.Resource;
import jakarta.servlet.RequestDispatcher; // <-- penting: constants ERROR_STATUS_CODE, dll

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GlobalErrorController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CommonWebConfig.class)
@ActiveProfiles("test")
class GlobalErrorControllerBranchesTest {

    @Resource MockMvc mvc;

    @Test
    void notFound_404_routedToErrorController() throws Exception {
        mvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.NOT_FOUND.value())
                        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/nope")
                        .requestAttr(RequestDispatcher.ERROR_MESSAGE, "Not Found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("not_found"))
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void methodNotAllowed_405() throws Exception {
        mvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.METHOD_NOT_ALLOWED.value())
                        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/t/echo")
                        .requestAttr(RequestDispatcher.ERROR_MESSAGE, "Method Not Allowed"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value("method_not_allowed"));
    }

    @Test
    void unsupportedMediaType_415() throws Exception {
        mvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/t/echo")
                        .requestAttr(RequestDispatcher.ERROR_MESSAGE, "Unsupported Media Type"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.code").value("unsupported_media_type"));
    }

    @Test
    void notAcceptable_406() throws Exception {
        mvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.NOT_ACCEPTABLE.value())
                        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/t/echo")
                        .requestAttr(RequestDispatcher.ERROR_MESSAGE, "Not Acceptable"))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.code").value("not_acceptable"));
    }

    @Test
    void default4xx_badRequest_400() throws Exception {
        mvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.BAD_REQUEST.value())
                        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/t/echo")
                        .requestAttr(RequestDispatcher.ERROR_MESSAGE, "Bad Request"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad_request"));
    }

    @Test
    void serverError_default500_internalError() throws Exception {
        mvc.perform(get("/error")
                        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/t/boom")
                        .requestAttr(RequestDispatcher.ERROR_MESSAGE, "Internal Server Error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("internal_error"));
    }
}
