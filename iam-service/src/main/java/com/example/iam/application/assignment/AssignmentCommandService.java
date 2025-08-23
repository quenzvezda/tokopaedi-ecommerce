package com.example.iam.application.assignment;

import com.example.iam.domain.assignment.RolePermissionRepository;
import com.example.iam.domain.entitlement.EntitlementVersionRepository;
import com.example.iam.domain.user.UserRoleRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class AssignmentCommandService implements AssignmentCommands {
    private final RolePermissionRepository rolePerm;
    private final UserRoleRepository userRole;
    private final EntitlementVersionRepository version;

    @Override
    public void assignPermissionToRole(Long roleId, Long permissionId) {
        if (!rolePerm.exists(roleId, permissionId)) rolePerm.add(roleId, permissionId);
        userRole.findByRoleId(roleId).forEach(ur -> version.bump(ur.getAccountId()));
    }

    @Override
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        if (rolePerm.exists(roleId, permissionId)) rolePerm.remove(roleId, permissionId);
        userRole.findByRoleId(roleId).forEach(ur -> version.bump(ur.getAccountId()));
    }

    @Override
    public void assignRoleToUser(UUID accountId, Long roleId) {
        if (!userRole.exists(accountId, roleId)) userRole.add(accountId, roleId);
        version.bump(accountId);
    }

    @Override
    public void removeRoleFromUser(UUID accountId, Long roleId) {
        if (userRole.exists(accountId, roleId)) userRole.remove(accountId, roleId);
        version.bump(accountId);
    }
}
