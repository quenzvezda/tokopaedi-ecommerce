package com.example.profile.web;

import com.example.common.web.error.ApiException;
import com.example.common.web.response.ErrorProps;
import com.example.common.web.response.ErrorResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        ErrorProps props = new ErrorProps();
        props.setVerbose(false);
        handler = new GlobalExceptionHandler(new ErrorResponseBuilder(props, "profile-service"));
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
        request.setMethod("GET");
    }

    @Test
    void apiException_returnsCustomStatus() {
        ApiException ex = ApiException.conflict("conflict", "Conflict");
        ResponseEntity<?> resp = handler.apiException(request, ex);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void badJson_returns400() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("bad", new ServletServerHttpRequest(request));
        ResponseEntity<?> resp = handler.badJson(request, ex);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void beanValidation_returnsFieldErrors() throws Exception {
        Method m = Dummy.class.getDeclaredMethod("dummy", String.class);
        MethodParameter param = new MethodParameter(m, 0);
        BeanPropertyBindingResult result = new BeanPropertyBindingResult("t", "t");
        result.addError(new FieldError("t", "field", "message"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(param, result);

        ResponseEntity<?> resp = handler.beanValidation(request, ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    static class Dummy { void dummy(String s) {} }

    @Test
    void constraintViolation_returns400() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(PathImpl.createPathFromString("field"));
        when(violation.getMessage()).thenReturn("invalid");
        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<?> resp = handler.constraintViolation(request, ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void notFound_returns404() {
        ResponseEntity<?> resp = handler.notFound(request, new NoSuchElementException());
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void methodNotAllowed_returns405() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST", List.of("GET"));
        ResponseEntity<?> resp = handler.methodNotAllowed(request, ex);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    void unsupportedMediaType_returns415() {
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException(MediaType.APPLICATION_XML, List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<?> resp = handler.unsupportedMediaType(request, ex);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    void dataConflict_returns409() {
        ResponseEntity<?> resp = handler.dataConflict(request, new DataIntegrityViolationException("x"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void accessDenied_returns403() {
        ResponseEntity<?> resp = handler.accessDenied(request, new AccessDeniedException("denied"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void unknown_returns500() {
        ResponseEntity<?> resp = handler.unknown(request, new Exception("boom"));
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void respond_handlesNullException() throws Exception {
        Method respond = GlobalExceptionHandler.class.getDeclaredMethod("respond", HttpServletRequest.class, HttpStatus.class, String.class, String.class, Throwable.class, Map.class);
        respond.setAccessible(true);

        ResponseEntity<?> resp = (ResponseEntity<?>) respond.invoke(handler, request, HttpStatus.BAD_REQUEST, "code", "msg", null, null);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
