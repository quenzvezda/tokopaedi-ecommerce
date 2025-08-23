package com.example.iam.application.command;

import com.example.iam.domain.entitlement.EntitlementVersionRepository;
import com.example.iam.domain.assignment.RolePermissionRepository;
import com.example.iam.domain.user.UserRoleRepository;

public class AssignPermissionToRoleCommand {
    private final RolePermissionRepository rolePerm;
    private final UserRoleRepository userRole;
    private final EntitlementVersionRepository version;
    public AssignPermissionToRoleCommand(RolePermissionRepository rolePerm, UserRoleRepository userRole, EntitlementVersionRepository version) { this.rolePerm = rolePerm; this.userRole = userRole; this.version = version; }
    public void handle(Long roleId, Long permissionId) {
        if (!rolePerm.exists(roleId, permissionId)) rolePerm.add(roleId, permissionId);
        userRole.findByRoleId(roleId).forEach(ur -> version.bump(ur.getAccountId()));
    }
}
