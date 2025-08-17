package com.example.common.web;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

public class RequestIdFilter implements Filter {
    public static final String HEADER = "X-Request-Id";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) req;
        HttpServletResponse w = (HttpServletResponse) res;

        String rid = r.getHeader(HEADER);
        if (rid == null || rid.isBlank()) rid = shortId();
        MDC.put("requestId", rid);
        w.setHeader(HEADER, rid);
        try {
            chain.doFilter(req, res);
        } finally {
            MDC.remove("requestId");
        }
    }

    private String shortId() {
        String s = UUID.randomUUID().toString();
        int idx = s.indexOf('-');
        return idx > 0 ? s.substring(0, idx) : s;
    }
}
