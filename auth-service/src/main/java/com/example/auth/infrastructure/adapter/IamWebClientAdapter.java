package com.example.auth.infrastructure.adapter;

import com.example.auth.domain.model.Entitlements;
import com.example.auth.domain.port.IamPort;
import com.example.auth.web.error.IamUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class IamWebClientAdapter implements IamPort {
    private final WebClient iamWebClient;
    private final int responseMs;
    private final String serviceToken;

    @Override
    public Entitlements fetchEntitlements(UUID accountId) {
        Mono<Map> ent = iamWebClient.get()
                .uri("/internal/v1/entitlements/{id}", accountId)
                .header("X-Internal-Token", serviceToken)
                .retrieve().bodyToMono(Map.class);

        Mono<List> roles = iamWebClient.get()
                .uri("/internal/v1/users/{id}/roles", accountId)
                .header("X-Internal-Token", serviceToken)
                .retrieve().bodyToMono(List.class);

        try {
            return Mono.zip(ent, roles)
                    .map(t -> toEntitlements(accountId, t.getT1(), t.getT2()))
                    .timeout(Duration.ofMillis(responseMs))
                    .block(Duration.ofSeconds(1));
        } catch (WebClientRequestException e) {
            throw new IamUnavailableException("connection_error");
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is5xxServerError()) throw new IamUnavailableException("upstream_5xx");
            throw new IamUnavailableException("upstream_4xx");
        } catch (Exception e) {
            throw new IamUnavailableException("unknown");
        }
    }

    private Entitlements toEntitlements(UUID accountId, Map ent, List roleList) {
        Object v = ent.get("perm_ver");
        int ver = v instanceof Number ? ((Number) v).intValue() : Integer.parseInt(String.valueOf(v));
        @SuppressWarnings("unchecked")
        List<String> roles = roleList == null ? List.of() : (List<String>) roleList;
        return Entitlements.of(accountId, ver, roles, Instant.now());
    }
}
