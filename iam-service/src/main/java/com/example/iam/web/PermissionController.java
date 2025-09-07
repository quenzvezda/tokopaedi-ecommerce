package com.example.iam.web;

import com.example.iam.application.permission.PermissionCommands;
import com.example.iam.application.permission.PermissionQueries;
import com.example.iam.domain.permission.Permission;
import com.example.iam_service.web.api.PermissionApi;
import com.example.iam_service.web.model.PermissionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PermissionController implements PermissionApi {
    private final PermissionCommands commands;
    private final PermissionQueries queries;

    @Override
    public ResponseEntity<com.example.iam_service.web.model.Permission> createPermission(PermissionRequest permissionRequest) {
        Permission p = commands.create(permissionRequest.getName(), permissionRequest.getDescription());
        return ResponseEntity.ok(map(p));
    }

    @Override
    public ResponseEntity<Void> deletePermission(Long id) {
        commands.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<com.example.iam_service.web.model.Permission> getPermission(Long id) {
        return ResponseEntity.ok(map(queries.getById(id)));
    }

    @Override
    public ResponseEntity<List<com.example.iam_service.web.model.Permission>> listPermissions() {
        return ResponseEntity.ok(queries.list().stream().map(PermissionController::map).toList());
    }

    @Override
    public ResponseEntity<com.example.iam_service.web.model.Permission> updatePermission(Long id, PermissionRequest permissionRequest) {
        Permission p = commands.update(id, permissionRequest.getName(), permissionRequest.getDescription());
        return ResponseEntity.ok(map(p));
    }

    private static com.example.iam_service.web.model.Permission map(Permission p) {
        return new com.example.iam_service.web.model.Permission().id(p.getId()).name(p.getName()).description(p.getDescription());
    }
}
