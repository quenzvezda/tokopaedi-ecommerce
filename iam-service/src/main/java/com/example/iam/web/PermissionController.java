package com.example.iam.web;

import com.example.iam.application.permission.PermissionCommands;
import com.example.iam.application.permission.PermissionQueries;
import com.example.iam.domain.permission.Permission;
import com.example.iam.web.dto.PermissionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "1. Permission")
public class PermissionController {
    private final PermissionCommands commands;
    private final PermissionQueries queries;

    @GetMapping
    @Operation(operationId = "permission_1_list", summary = "List permissions", security = {@SecurityRequirement(name = "bearer-key")})
    public List<Permission> all() { return queries.list(); }

    @GetMapping("/{id}")
    @Operation(operationId = "permission_2_get", summary = "Get permission", security = {@SecurityRequirement(name = "bearer-key")})
    public Permission get(@PathVariable Long id) { return queries.getById(id); }

    @PostMapping
    @Operation(operationId = "permission_3_create", summary = "Create permission", security = {@SecurityRequirement(name = "bearer-key")})
    public Permission create(@Valid @RequestBody PermissionRequest req) {
        return commands.create(req.getName(), req.getDescription());
    }

    @PutMapping("/{id}")
    @Operation(operationId = "permission_4_update", summary = "Update permission", security = {@SecurityRequirement(name = "bearer-key")})
    public Permission update(@PathVariable Long id, @Valid @RequestBody PermissionRequest req) {
        return commands.update(id, req.getName(), req.getDescription());
    }

    @DeleteMapping("/{id}")
    @Operation(operationId = "permission_5_delete", summary = "Delete permission", security = {@SecurityRequirement(name = "bearer-key")})
    public void delete(@PathVariable Long id) { commands.delete(id); }
}
