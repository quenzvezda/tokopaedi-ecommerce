package com.example.common.web.security;

import com.example.common.web.response.ApiErrorResponse;
import com.example.common.web.response.ErrorResponseBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JsonAuthEntryPoint implements AuthenticationEntryPoint {
    private static final Logger log = LoggerFactory.getLogger(JsonAuthEntryPoint.class);

    private final ErrorResponseBuilder builder;
    private final ObjectMapper om;

    public JsonAuthEntryPoint(ErrorResponseBuilder builder, ObjectMapper om) {
        this.builder = builder;
        this.om = om;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException {
        ApiErrorResponse body = builder.build(
                request,
                HttpStatus.UNAUTHORIZED,
                "UNAUTHORIZED",
                "Authentication required",
                ex,
                null,
                log
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setHeader("WWW-Authenticate", "Bearer"); // RFC 6750
        response.getWriter().write(om.writeValueAsString(body));
    }
}
