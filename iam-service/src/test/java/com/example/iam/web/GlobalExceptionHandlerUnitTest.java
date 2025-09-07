package com.example.iam.web;

import com.example.common.web.response.ErrorProps;
import com.example.common.web.response.ErrorResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerUnitTest {

    GlobalExceptionHandler handler;
    HttpServletRequest req;

    @BeforeEach
    void setUp() {
        var props = new ErrorProps();
        props.setVerbose(false);
        var builder = new ErrorResponseBuilder(props, "iam-service");
        handler = new GlobalExceptionHandler(builder);
        var r = new MockHttpServletRequest("GET", "/test");
        r.setRemoteAddr("127.0.0.1");
        req = r;
    }

    @Test
    void methodNotAllowed_405() {
        ResponseEntity<?> resp = handler.methodNotAllowed(req, new HttpRequestMethodNotSupportedException("POST"));
        assertThat(resp.getStatusCode().value()).isEqualTo(405);
    }

    @Test
    void unsupportedMediaType_415() {
        ResponseEntity<?> resp = handler.unsupportedMediaType(req, new HttpMediaTypeNotSupportedException("xml"));
        assertThat(resp.getStatusCode().value()).isEqualTo(415);
    }

    @Test
    void dataConflict_409() {
        ResponseEntity<?> resp = handler.dataConflict(req, new DataIntegrityViolationException("x"));
        assertThat(resp.getStatusCode().value()).isEqualTo(409);
    }

    @Test
    void accessDenied_403() {
        ResponseEntity<?> resp = handler.accessDenied(req, new AccessDeniedException("no"));
        assertThat(resp.getStatusCode().value()).isEqualTo(403);
    }

    @Test
    void notFound_404() {
        ResponseEntity<?> resp = handler.notFound(req, new java.util.NoSuchElementException());
        assertThat(resp.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void badJson_400() {
        ResponseEntity<?> resp = handler.badJson(req, new HttpMessageNotReadableException("bad"));
        assertThat(resp.getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void unknown_500() {
        ResponseEntity<?> resp = handler.unknown(req, new RuntimeException("boom"));
        assertThat(resp.getStatusCode().value()).isEqualTo(500);
    }
}

