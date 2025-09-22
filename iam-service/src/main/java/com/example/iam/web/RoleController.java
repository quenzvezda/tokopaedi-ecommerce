package com.example.iam.web;

import com.example.iam.application.role.RoleCommands;
import com.example.iam.application.role.RoleQueries;
import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.role.Role;
import com.example.iam_service.web.api.RoleApi;
import com.example.iam_service.web.model.RoleRequest;
import com.example.iam_service.web.model.RolePage;
import com.example.iam_service.web.model.PermissionPage;
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
    public ResponseEntity<java.util.List<com.example.iam_service.web.model.Role>> listRoles() {
        var pr = queries.list(0, Integer.MAX_VALUE);
        return ResponseEntity.ok(pr.content().stream().map(RoleController::map).toList());
    }

    @Override
    public ResponseEntity<RolePage> listRolesV2(Integer page, Integer size, String q, List<String> sort) {
        var pr = queries.search(q, sort, page == null ? 0 : page, size == null ? 20 : size);
        var content = pr.content().stream().map(RoleController::map).toList();
        var body = new RolePage()
                .content(content)
                .number(pr.page())
                .size(pr.size())
                .totalElements((int) Math.min(Integer.MAX_VALUE, Math.max(0, pr.totalElements())))
                .totalPages(pr.totalPages());
        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<com.example.iam_service.web.model.Role> updateRole(Long id, RoleRequest roleRequest) {
        Role r = commands.update(id, roleRequest.getName());
        return ResponseEntity.ok(map(r));
    }

    @Override
    public ResponseEntity<java.util.List<com.example.iam_service.web.model.Permission>> listRolePermissions(Long roleId, Boolean available) {
        var pr = queries.listPermissions(roleId, available, 0, Integer.MAX_VALUE);
        return ResponseEntity.ok(pr.content().stream().map(RoleController::map).toList());
    }

    @Override
    public ResponseEntity<PermissionPage> listRolePermissionsV2(Long roleId, Boolean available, Integer page, Integer size) {
        var pr = queries.listPermissions(roleId, available, page == null ? 0 : page, size == null ? 20 : size);
        var content = pr.content().stream().map(RoleController::map).toList();
        var body = new PermissionPage()
                .content(content)
                .number(pr.page())
                .size(pr.size())
                .totalElements((int) Math.min(Integer.MAX_VALUE, Math.max(0, pr.totalElements())))
                .totalPages(pr.totalPages());
        return ResponseEntity.ok(body);
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
