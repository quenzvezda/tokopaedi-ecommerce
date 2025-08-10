package com.example.auth.infrastructure.adapter;

import com.example.auth.domain.model.Entitlements;
import com.example.auth.domain.port.IamPort;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IamWebClientAdapter implements IamPort {
    private final WebClient iamWebClient;
    private final CircuitBreakerRegistry cbRegistry;
    private final RetryRegistry retryRegistry;

    @Value("${iam.http.response-timeout-ms}") private int responseMs;

    @Override
    public Entitlements fetchEntitlements(UUID accountId) {
        Mono<Map> ent = iamWebClient.get().uri("/entitlements/{id}", accountId).retrieve().bodyToMono(Map.class);
        Mono<List> roles = iamWebClient.get().uri("/users/{id}/roles", accountId).retrieve().bodyToMono(List.class);
        var cb = cbRegistry.circuitBreaker("iam");
        var rt = retryRegistry.retry("iam");
        return Mono.zip(ent, roles)
                .map(t -> toEntitlements(accountId, t.getT1(), t.getT2()))
                .transformDeferred(CircuitBreakerOperator.of(cb))
                .transformDeferred(RetryOperator.of(rt))
                .timeout(Duration.ofMillis(responseMs))
                .block(Duration.ofSeconds(1));
    }

    private Entitlements toEntitlements(UUID accountId, Map ent, List roleList) {
        Object v = ent.get("perm_ver");
        int permVer = v instanceof Number ? ((Number)v).intValue() : Integer.parseInt(String.valueOf(v));
        @SuppressWarnings("unchecked")
        List<String> roles = roleList == null ? List.of() : (List<String>) roleList;
        return Entitlements.of(accountId, permVer, roles, Instant.now());
    }
}
