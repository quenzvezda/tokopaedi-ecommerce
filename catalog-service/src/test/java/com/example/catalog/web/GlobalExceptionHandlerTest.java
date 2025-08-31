package com.example.catalog.web;

import com.example.common.web.response.ErrorProps;
import com.example.common.web.response.ErrorResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.core.MethodParameter;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setup() {
        var builder = new ErrorResponseBuilder(new ErrorProps(), "catalog-service");
        handler = new GlobalExceptionHandler(builder);
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
        request.setMethod("GET");
    }

    @Test
    void badJson_returns400() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("bad", new ServletServerHttpRequest(request));
        ResponseEntity<?> resp = handler.badJson(request, ex);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void beanValidation_returns400() throws Exception {
        Method m = Dummy.class.getDeclaredMethod("dummy", String.class);
        MethodParameter param = new MethodParameter(m, 0);
        var br = new BeanPropertyBindingResult("t", "t");
        br.addError(new FieldError("t", "field", "message"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(param, br);
        ResponseEntity<?> resp = handler.beanValidation(request, ex);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    static class Dummy { void dummy(String s) {} }

    @Test
    void notFound_returns404() {
        ResponseEntity<?> resp = handler.notFound(request, new NoSuchElementException());
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    void methodNotAllowed_returns405() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST", List.of("GET"));
        ResponseEntity<?> resp = handler.methodNotAllowed(request, ex);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, resp.getStatusCode());
    }

    @Test
    void unsupportedMediaType_returns415() {
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException(MediaType.APPLICATION_XML, List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<?> resp = handler.unsupportedMediaType(request, ex);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, resp.getStatusCode());
    }

    @Test
    void dataConflict_returns409() {
        ResponseEntity<?> resp = handler.dataConflict(request, new DataIntegrityViolationException("x"));
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
    }

    @Test
    void accessDenied_returns403() {
        ResponseEntity<?> resp = handler.accessDenied(request, new AccessDeniedException("denied"));
        assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    }

    @Test
    void unknown_returns500() {
        ResponseEntity<?> resp = handler.unknown(request, new Exception("boom"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }

    @Test
    void respond_nullException_branch() throws Exception {
        Method m = GlobalExceptionHandler.class.getDeclaredMethod("respond", HttpServletRequest.class, HttpStatus.class, String.class, String.class, Throwable.class, Map.class);
        m.setAccessible(true);
        ResponseEntity<?> resp = (ResponseEntity<?>) m.invoke(handler, request, HttpStatus.BAD_REQUEST, "code", "msg", null, null);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }
}

