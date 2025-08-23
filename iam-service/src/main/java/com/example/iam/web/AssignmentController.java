package com.example.iam.web;

import com.example.iam.application.assignment.AssignmentCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assign")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentCommands commands;

    @PostMapping("/role/{roleId}/permission/{permissionId}")
    public Map<String, String> addPermissionToRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        commands.assignPermissionToRole(roleId, permissionId);
        return Map.of("message","ok");
    }

    @DeleteMapping("/role/{roleId}/permission/{permissionId}")
    public Map<String, String> removePermissionFromRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        commands.removePermissionFromRole(roleId, permissionId);
        return Map.of("message","ok");
    }

    @PostMapping("/user/{accountId}/role/{roleId}")
    public Map<String, String> addRoleToUser(@PathVariable UUID accountId, @PathVariable Long roleId) {
        commands.assignRoleToUser(accountId, roleId);
        return Map.of("message","ok");
    }

    @DeleteMapping("/user/{accountId}/role/{roleId}")
    public Map<String, String> removeRoleFromUser(@PathVariable UUID accountId, @PathVariable Long roleId) {
        commands.removeRoleFromUser(accountId, roleId);
        return Map.of("message","ok");
    }
}
