package com.example.iam.application.command;

import com.example.iam.domain.port.EntitlementVersionPort;
import com.example.iam.domain.port.UserRolePort;

import java.util.UUID;

public class RemoveRoleFromUserCommand {
    private final UserRolePort userRole;
    private final EntitlementVersionPort version;
    public RemoveRoleFromUserCommand(UserRolePort userRole, EntitlementVersionPort version) { this.userRole = userRole; this.version = version; }
    public void handle(UUID accountId, Long roleId) {
        if (userRole.exists(accountId, roleId)) userRole.remove(accountId, roleId);
        version.bump(accountId);
    }
}
