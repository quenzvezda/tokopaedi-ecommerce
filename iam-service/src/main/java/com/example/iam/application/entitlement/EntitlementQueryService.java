package com.example.iam.application.entitlement;

import com.example.iam.domain.assignment.RolePermissionRepository;
import com.example.iam.domain.entitlement.EntitlementVersionRepository;
import com.example.iam.domain.permission.PermissionRepository;
import com.example.iam.domain.user.UserRole;
import com.example.iam.domain.user.UserRoleRepository;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EntitlementQueryService implements EntitlementQueries {
    private final UserRoleRepository userRole;
    private final RolePermissionRepository rolePerm;
    private final PermissionRepository permission;
    private final EntitlementVersionRepository version;

    @Override
    public Map<String, Object> getEntitlements(UUID accountId) {
        List<UserRole> urs = userRole.findByAccountId(accountId);
        if (urs.isEmpty()) {
            return Map.of("perm_ver", version.getOrInit(accountId), "scopes", List.of());
        }
        Set<Long> roleIds = urs.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        Set<Long> permIds = new HashSet<>();
        for (Long rid : roleIds) permIds.addAll(rolePerm.findPermissionIdsByRoleId(rid));
        if (permIds.isEmpty()) {
            return Map.of("perm_ver", version.getOrInit(accountId), "scopes", List.of());
        }
        List<String> scopes = permission.findAllByIds(permIds).stream()
                .map(p -> p.getName().trim())
                .filter(s -> !s.isBlank())
                .distinct()
                .sorted()
                .toList();

        return Map.of("perm_ver", version.getOrInit(accountId), "scopes", scopes);
    }

    @Override
    public Map<String, Object> checkAuthorization(UUID sub, String action) {
        Map<String, Object> ent = getEntitlements(sub);
        int entV = ((Number) ent.getOrDefault("perm_ver", 1)).intValue();

        @SuppressWarnings("unchecked")
        List<String> scopes = (List<String>) ent.getOrDefault("scopes", List.of());

        boolean allowed = scopes.contains(action);
        return Map.of("decision", allowed ? "ALLOW" : "DENY", "ent_v", entV);
    }
}
