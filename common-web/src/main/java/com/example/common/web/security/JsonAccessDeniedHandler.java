package com.example.common.web.security;

import com.example.common.web.ApiErrorResponse;
import com.example.common.web.ErrorResponseBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class JsonAccessDeniedHandler implements AccessDeniedHandler {
    private static final Logger log = LoggerFactory.getLogger(JsonAccessDeniedHandler.class);

    private final ErrorResponseBuilder builder;
    private final ObjectMapper om;

    public JsonAccessDeniedHandler(ErrorResponseBuilder builder, ObjectMapper om) {
        this.builder = builder;
        this.om = om;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        ApiErrorResponse body = builder.build(
                request,
                HttpStatus.FORBIDDEN,
                "FORBIDDEN",
                "Not allowed",
                ex,
                null,
                log
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.getWriter().write(om.writeValueAsString(body));
    }
}
