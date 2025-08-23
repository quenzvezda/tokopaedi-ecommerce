package com.example.iam.application.query;

import com.example.iam.domain.assignment.RolePermissionRepository;
import com.example.iam.domain.entitlement.EntitlementVersionRepository;
import com.example.iam.domain.user.UserRole;
import com.example.iam.domain.permission.PermissionRepository;
import com.example.iam.domain.user.UserRoleRepository;

import java.util.*;
import java.util.stream.Collectors;

public class GetEntitlementsQuery {
    private final UserRoleRepository userRole;
    private final RolePermissionRepository rolePerm;
    private final PermissionRepository permission;
    private final EntitlementVersionRepository version;

    public GetEntitlementsQuery(UserRoleRepository userRole, RolePermissionRepository rolePerm, PermissionRepository permission, EntitlementVersionRepository version) { this.userRole = userRole; this.rolePerm = rolePerm; this.permission = permission; this.version = version; }

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
