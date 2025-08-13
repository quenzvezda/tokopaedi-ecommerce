package com.example.iam.application.query;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class CheckAuthorizationQuery {

    private final GetEntitlementsQuery entitlementsQuery;

    /**
     * Mengembalikan { decision: "ALLOW"/"DENY", ent_v: <int> }.
     */
    public Map<String, Object> handle(UUID sub, String action) {
        Map<String, Object> ent = entitlementsQuery.handle(sub);

        int entV = ((Number) ent.getOrDefault("perm_ver", 1)).intValue();

        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) ent.getOrDefault("permissions", List.of());

        boolean allowed = permissions.contains(action);
        return Map.of(
                "decision", allowed ? "ALLOW" : "DENY",
                "ent_v", entV
        );
    }
}
