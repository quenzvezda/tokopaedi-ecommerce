package com.example.iam.web;

import com.example.iam.application.permission.PermissionCommands;
import com.example.iam.application.permission.PermissionCommands.CreatePermission;
import com.example.iam.application.permission.PermissionQueries;
import com.example.iam.domain.permission.Permission;
import com.example.iam_service.web.api.PermissionApi;
import com.example.iam_service.web.model.PermissionBulkRequest;
import com.example.iam_service.web.model.PermissionBulkResponse;
import com.example.iam_service.web.model.PermissionPage;
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
    public ResponseEntity<PermissionBulkResponse> createPermissionsBulk(PermissionBulkRequest permissionBulkRequest) {
        var created = commands.createBulk(permissionBulkRequest.getPermissions().stream()
                .map(dto -> new CreatePermission(dto.getName(), dto.getDescription()))
                .toList());
        var body = new PermissionBulkResponse()
                .created(created.stream().map(PermissionController::map).toList());
        return ResponseEntity.ok(body);
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
    public ResponseEntity<java.util.List<com.example.iam_service.web.model.Permission>> listPermissions() {
        var pr = queries.list(0, Integer.MAX_VALUE);
        return ResponseEntity.ok(pr.content().stream().map(PermissionController::map).toList());
    }

    @Override
    public ResponseEntity<PermissionPage> listPermissionsV2(Integer page, Integer size, String q, List<String> sort) {
        var pr = queries.search(q, sort, page == null ? 0 : page, size == null ? 20 : size);
        var content = pr.content().stream().map(PermissionController::map).toList();
        var body = new PermissionPage()
                .content(content)
                .number(pr.page())
                .size(pr.size())
                .totalElements((int) Math.min(Integer.MAX_VALUE, Math.max(0, pr.totalElements())))
                .totalPages(pr.totalPages());
        return ResponseEntity.ok(body);
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

