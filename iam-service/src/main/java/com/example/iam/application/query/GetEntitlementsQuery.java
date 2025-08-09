package com.example.iam.application.query;

import com.example.iam.domain.model.UserRole;
import com.example.iam.domain.port.*;
import java.util.*;
import java.util.stream.Collectors;

public class GetEntitlementsQuery {
    private final UserRolePort userRole;
    private final RolePermissionPort rolePerm;
    private final PermissionPort permission;
    private final EntitlementVersionPort version;

    public GetEntitlementsQuery(UserRolePort userRole, RolePermissionPort rolePerm, PermissionPort permission, EntitlementVersionPort version) { this.userRole = userRole; this.rolePerm = rolePerm; this.permission = permission; this.version = version; }

    public Map<String, Object> handle(UUID accountId) {
        List<UserRole> urs = userRole.findByAccountId(accountId);
        if (urs.isEmpty()) return Map.of("perm_ver", version.getOrInit(accountId), "scopes", List.of());
        Set<Long> roleIds = urs.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        Set<Long> permIds = new HashSet<>();
        for (Long rid : roleIds) permIds.addAll(rolePerm.findPermissionIdsByRoleId(rid));
        if (permIds.isEmpty()) return Map.of("perm_ver", version.getOrInit(accountId), "scopes", List.of());
        List<String> scopes = permission.findAllByIds(permIds).stream().map(p -> p.getName().trim()).filter(s -> !s.isBlank()).distinct().sorted().toList();
        return Map.of("perm_ver", version.getOrInit(accountId), "scopes", scopes);
    }
}
