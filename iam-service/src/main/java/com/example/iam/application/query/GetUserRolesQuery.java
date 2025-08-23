package com.example.iam.application.query;

import com.example.iam.domain.user.UserRole;
import com.example.iam.domain.role.RoleRepository;
import com.example.iam.domain.user.UserRoleRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class GetUserRolesQuery {
    private final UserRoleRepository userRole;
    private final RoleRepository role;
    public GetUserRolesQuery(UserRoleRepository userRole, RoleRepository role) { this.userRole = userRole; this.role = role; }
    public List<String> handle(UUID accountId) {
        List<UserRole> rs = userRole.findByAccountId(accountId);
        if (rs.isEmpty()) return List.of();
        Set<Long> ids = rs.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        return role.findAll().stream().filter(r -> ids.contains(r.getId())).map(r -> r.getName().trim()).filter(s -> !s.isBlank()).sorted().toList();
    }
}
