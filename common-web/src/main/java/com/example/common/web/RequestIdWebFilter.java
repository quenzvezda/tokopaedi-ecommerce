package com.example.common.web;

import org.slf4j.MDC;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class RequestIdWebFilter implements WebFilter {
    public static final String HEADER = "X-Request-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest req = exchange.getRequest();
        String rid = req.getHeaders().getFirst(HEADER);
        if (rid == null || rid.isBlank()) rid = shortId();

        exchange.getResponse().getHeaders().add(HEADER, rid);
        MDC.put("requestId", rid);
        return chain.filter(exchange)
                .doFinally(s -> MDC.remove("requestId"));
    }

    private String shortId() {
        String s = UUID.randomUUID().toString();
        int idx = s.indexOf('-');
        return idx > 0 ? s.substring(0, idx) : s;
    }
}
