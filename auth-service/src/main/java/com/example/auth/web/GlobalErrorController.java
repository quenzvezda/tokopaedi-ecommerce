package com.example.auth.web;

import com.example.common.web.response.ApiErrorResponse;
import com.example.common.web.response.ErrorResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Menggantikan BasicErrorController bawaan Spring.
 * Semua error yang jatuh ke /error (mis. 404 route tidak ditemukan, 405 method salah,
 * 415 content-type tidak didukung, dsb) akan dibungkus ke ApiErrorResponse JSON yang seragam.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("${server.error.path:/error}")
public class GlobalErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;      // DefaultErrorAttributes sudah disediakan Spring Boot
    private final ErrorResponseBuilder errors;          // dari modul common-web

    @RequestMapping
    public ResponseEntity<ApiErrorResponse> handleError(HttpServletRequest req, HttpServletResponse res) {
        var webReq = new ServletWebRequest(req);
        var attr = errorAttributes.getErrorAttributes(webReq, ErrorAttributeOptions.defaults());
        var throwable = errorAttributes.getError(webReq);  // bisa null untuk 404

        // Ambil status dari attributes atau dari HttpServletResponse
        int statusCode = safeInt(attr.get("status"), res.getStatus());
        HttpStatus status = HttpStatus.resolve(statusCode);
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;

        // Tentukan code + message yang seragam
        String code;
        String message;
        switch (status) {
            case NOT_FOUND -> {
                code = "not_found";
                message = "Resource not found";
            }
            case METHOD_NOT_ALLOWED -> {
                code = "method_not_allowed";
                message = "Method not allowed";
            }
            case NOT_ACCEPTABLE -> {
                code = "not_acceptable";
                message = "Not acceptable";
            }
            case UNSUPPORTED_MEDIA_TYPE -> {
                code = "unsupported_media_type";
                message = "Unsupported media type";
            }
            default -> {
                if (status.is4xxClientError()) {
                    code = "bad_request";
                    message = "Bad request";
                } else {
                    code = "internal_error";
                    message = "Unexpected error";
                }
            }
        }

        // Meta ringkas dari DefaultErrorAttributes -> akan masuk ke field "upstream"
        Map<String, Object> meta = new HashMap<>();
        putIfPresent(meta, "reason", attr.get("error"));          // mis. "Not Found", "Method Not Allowed"
        putIfPresent(meta, "path", attr.get("path"));             // path yang diminta
        putIfPresent(meta, "detail", attr.get("message"));        // pesan bawaan (singkat)
        // kalau mau, kamu bisa expose "exception" saat verbose=true, tapi biasanya tak perlu:
        // putIfPresent(meta, "exception", attr.get("exception"));

        var body = errors.build(
                req,
                status,
                code,
                message,
                throwable,           // null untuk 404 murni; ErrorResponseBuilder akan handle
                meta,
                LoggerFactory.getLogger(GlobalErrorController.class)
        );

        return ResponseEntity.status(status).body(body);
    }

    private static int safeInt(Object v, int fallback) {
        if (v instanceof Number n) return n.intValue();
        try { return Integer.parseInt(String.valueOf(v)); } catch (Exception ignore) {
            return fallback;
        }
    }

    private static void putIfPresent(Map<String, Object> m, String key, Object v) {
        if (v == null) return;
        String s = String.valueOf(v);
        if (!s.isBlank()) m.put(key, v);
    }
}
