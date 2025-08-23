package com.example.iam.application.assignment;

import java.util.UUID;

public interface AssignmentCommands {
    void assignPermissionToRole(Long roleId, Long permissionId);
    void removePermissionFromRole(Long roleId, Long permissionId);
    void assignRoleToUser(UUID accountId, Long roleId);
    void removeRoleFromUser(UUID accountId, Long roleId);
}
