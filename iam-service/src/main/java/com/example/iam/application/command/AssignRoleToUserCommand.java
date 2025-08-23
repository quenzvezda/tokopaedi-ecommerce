package com.example.iam.application.command;

import com.example.iam.domain.entitlement.EntitlementVersionRepository;
import com.example.iam.domain.user.UserRoleRepository;

import java.util.UUID;

public class AssignRoleToUserCommand {
    private final UserRoleRepository userRole;
    private final EntitlementVersionRepository version;
    public AssignRoleToUserCommand(UserRoleRepository userRole, EntitlementVersionRepository version) { this.userRole = userRole; this.version = version; }
    public void handle(UUID accountId, Long roleId) {
        if (!userRole.exists(accountId, roleId)) userRole.add(accountId, roleId);
        version.bump(accountId);
    }
}
