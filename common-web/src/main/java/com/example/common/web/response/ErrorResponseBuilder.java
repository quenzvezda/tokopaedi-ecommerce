package com.example.common.web.response;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Map;

public class ErrorResponseBuilder {
    private final ErrorProps props;
    private final String serviceName;

    public ErrorResponseBuilder(ErrorProps props, String serviceName) {
        this.props = props;
        this.serviceName = serviceName;
    }

    public ApiErrorResponse build(HttpServletRequest req,
                                  HttpStatus status,
                                  String code,
                                  String message,
                                  Throwable ex,
                                  Map<String, Object> upstream,
                                  Logger log) {

        if (ex != null) {
            if (status.is5xxServerError())
                log.error("[{}] {} {} -> {} {} : {}", code, req.getMethod(), req.getRequestURI(),
                        status.value(), status.getReasonPhrase(), ex, ex);
            else
                log.warn("[{}] {} {} -> {} {} : {}", code, req.getMethod(), req.getRequestURI(),
                        status.value(), status.getReasonPhrase(), ex.toString());
        } else {
            if (status.is5xxServerError())
                log.error("[{}] {} {} -> {} {}", code, req.getMethod(), req.getRequestURI(),
                        status.value(), status.getReasonPhrase());
            else
                log.warn("[{}] {} {} -> {} {}", code, req.getMethod(), req.getRequestURI(),
                        status.value(), status.getReasonPhrase());
        }

        String details = null;
        if (props != null && props.isVerbose() && ex != null) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            details = sw.toString();
        }

        return ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .service(serviceName)
                .path(req.getRequestURI())
                .method(req.getMethod())
                .status(status.value())
                .code(code)
                .message(message)
                .requestId(MDC.get("requestId"))
                .upstream(upstream)
                .details(details)
                .build();
    }
}
