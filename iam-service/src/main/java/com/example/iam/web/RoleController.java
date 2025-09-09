package com.example.iam.web;

import com.example.iam.application.role.RoleCommands;
import com.example.iam.application.role.RoleQueries;
import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.role.Role;
import com.example.iam_service.web.api.RoleApi;
import com.example.iam_service.web.model.RoleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RoleController implements RoleApi {
    private final RoleCommands commands;
    private final RoleQueries queries;

    @Override
    public ResponseEntity<com.example.iam_service.web.model.Role> createRole(RoleRequest roleRequest) {
        Role r = commands.create(roleRequest.getName());
        return ResponseEntity.ok(map(r));
    }

    @Override
    public ResponseEntity<Void> deleteRole(Long id) {
        commands.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<com.example.iam_service.web.model.Role> getRole(Long id) {
        return ResponseEntity.ok(map(queries.getById(id)));
    }

    @Override
    public ResponseEntity<List<com.example.iam_service.web.model.Role>> listRoles() {
        return ResponseEntity.ok(queries.list().stream().map(RoleController::map).toList());
    }

    @Override
    public ResponseEntity<com.example.iam_service.web.model.Role> updateRole(Long id, RoleRequest roleRequest) {
        Role r = commands.update(id, roleRequest.getName());
        return ResponseEntity.ok(map(r));
    }

    @Override
    public ResponseEntity<List<com.example.iam_service.web.model.Permission>> listRolePermissions(Long roleId) {
        return ResponseEntity.ok(queries.listPermissions(roleId).stream().map(RoleController::map).toList());
    }

    @Override
    public ResponseEntity<List<com.example.iam_service.web.model.Permission>> listAvailableRolePermissions(Long roleId) {
        return ResponseEntity.ok(queries.listAvailablePermissions(roleId).stream().map(RoleController::map).toList());
    }

    private static com.example.iam_service.web.model.Role map(Role r) {
        return new com.example.iam_service.web.model.Role().id(r.getId()).name(r.getName());
    }

    private static com.example.iam_service.web.model.Permission map(Permission p) {
        return new com.example.iam_service.web.model.Permission()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription());
    }
}
