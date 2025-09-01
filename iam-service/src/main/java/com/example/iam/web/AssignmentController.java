package com.example.iam.web;

import com.example.iam.application.assignment.AssignmentCommands;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assign")
@RequiredArgsConstructor
@Tag(name = "3. Assignment")
public class AssignmentController {
    private final AssignmentCommands commands;

    @PostMapping("/role/{roleId}/permission/{permissionId}")
    @Operation(operationId = "assignment_1_add_permission_to_role", summary = "Add permission to role", security = {@SecurityRequirement(name = "bearer-key")})
    public Map<String, String> addPermissionToRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        commands.assignPermissionToRole(roleId, permissionId);
        return Map.of("message","ok");
    }

    @DeleteMapping("/role/{roleId}/permission/{permissionId}")
    @Operation(operationId = "assignment_2_remove_permission_from_role", summary = "Remove permission from role", security = {@SecurityRequirement(name = "bearer-key")})
    public Map<String, String> removePermissionFromRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        commands.removePermissionFromRole(roleId, permissionId);
        return Map.of("message","ok");
    }

    @PostMapping("/user/{accountId}/role/{roleId}")
    @Operation(operationId = "assignment_3_add_role_to_user", summary = "Add role to user", security = {@SecurityRequirement(name = "bearer-key")})
    public Map<String, String> addRoleToUser(@PathVariable UUID accountId, @PathVariable Long roleId) {
        commands.assignRoleToUser(accountId, roleId);
        return Map.of("message","ok");
    }

    @DeleteMapping("/user/{accountId}/role/{roleId}")
    @Operation(operationId = "assignment_4_remove_role_from_user", summary = "Remove role from user", security = {@SecurityRequirement(name = "bearer-key")})
    public Map<String, String> removeRoleFromUser(@PathVariable UUID accountId, @PathVariable Long roleId) {
        commands.removeRoleFromUser(accountId, roleId);
        return Map.of("message","ok");
    }
}
