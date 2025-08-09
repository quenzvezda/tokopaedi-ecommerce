package com.example.iam.application.command;

import com.example.iam.domain.port.EntitlementVersionPort;
import com.example.iam.domain.port.RolePermissionPort;
import com.example.iam.domain.port.UserRolePort;

public class RemovePermissionFromRoleCommand {
    private final RolePermissionPort rolePerm;
    private final UserRolePort userRole;
    private final EntitlementVersionPort version;
    public RemovePermissionFromRoleCommand(RolePermissionPort rolePerm, UserRolePort userRole, EntitlementVersionPort version) { this.rolePerm = rolePerm; this.userRole = userRole; this.version = version; }
    public void handle(Long roleId, Long permissionId) {
        if (rolePerm.exists(roleId, permissionId)) rolePerm.remove(roleId, permissionId);
        userRole.findByRoleId(roleId).forEach(ur -> version.bump(ur.getAccountId()));
    }
}
