package com.example.iam.web;

import com.example.iam.application.assignment.AssignmentCommands;
import com.example.iam_service.web.api.AssignmentApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AssignmentController implements AssignmentApi {
    private final AssignmentCommands commands;

    @Override
    public ResponseEntity<Void> addPermissionToRole(Long roleId, Long permissionId) {
        commands.assignPermissionToRole(roleId, permissionId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> removePermissionFromRole(Long roleId, Long permissionId) {
        commands.removePermissionFromRole(roleId, permissionId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> addRoleToUser(UUID accountId, Long roleId) {
        commands.assignRoleToUser(accountId, roleId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> removeRoleFromUser(UUID accountId, Long roleId) {
        commands.removeRoleFromUser(accountId, roleId);
        return ResponseEntity.ok().build();
    }
}
