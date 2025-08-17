package com.example.iam.web;

import com.example.iam.application.command.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assign")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignPermissionToRoleCommand addPermToRole;
    private final RemovePermissionFromRoleCommand removePermFromRole;
    private final AssignRoleToUserCommand addRoleToUser;
    private final RemoveRoleFromUserCommand removeRoleFromUser;

    @PostMapping("/role/{roleId}/permission/{permissionId}")
    public Map<String, String> addPermissionToRole(@PathVariable("roleId") Long roleId, @PathVariable("permissionId") Long permissionId) { addPermToRole.handle(roleId, permissionId); return Map.of("message","ok"); }

    @DeleteMapping("/role/{roleId}/permission/{permissionId}")
    public Map<String, String> removePermissionFromRole(@PathVariable("roleId") Long roleId, @PathVariable("permissionId") Long permissionId) { removePermFromRole.handle(roleId, permissionId); return Map.of("message","ok"); }

    @PostMapping("/user/{accountId}/role/{roleId}")
    public Map<String, String> addRoleToUser(@PathVariable("accountId") UUID accountId, @PathVariable("roleId") Long roleId) { addRoleToUser.handle(accountId, roleId); return Map.of("message","ok"); }

    @DeleteMapping("/user/{accountId}/role/{roleId}")
    public Map<String, String> removeRoleFromUser(@PathVariable("accountId") UUID accountId, @PathVariable("roleId") Long roleId) { removeRoleFromUser.handle(accountId, roleId); return Map.of("message","ok"); }
}
