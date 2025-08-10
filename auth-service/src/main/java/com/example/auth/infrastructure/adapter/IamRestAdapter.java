package com.example.auth.infrastructure.adapter;

import com.example.auth.domain.port.IamPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IamRestAdapter implements IamPort {
    private final RestTemplate http;
    @Value("${iam.base-url}")
    private String baseUrl;

    @Override
    public int getPermVersion(UUID accountId) {
        Map res = http.getForObject(baseUrl + "/entitlements/{id}", Map.class, accountId);
        Object v = res.get("perm_ver");
        return v instanceof Number ? ((Number) v).intValue() : Integer.parseInt(String.valueOf(v));
    }

    @Override
    public List<String> getUserRoles(UUID accountId) {
        List list = http.getForObject(baseUrl + "/users/{id}/roles", List.class, accountId);
        return list == null ? List.of() : (List<String>) list;
    }
}
