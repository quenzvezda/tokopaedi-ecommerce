package com.example.iam.web;

import com.example.iam.application.role.RoleCommands;
import com.example.iam.application.role.RoleQueries;
import com.example.iam.domain.role.Role;
import com.example.iam.web.dto.RoleRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "2. Role")
public class RoleController {
    private final RoleCommands commands;
    private final RoleQueries queries;

    @GetMapping
    @Operation(operationId = "role_1_list", summary = "List roles", security = {@SecurityRequirement(name = "bearer-key")})
    public List<Role> all() { return queries.list(); }

    @GetMapping("/{id}")
    @Operation(operationId = "role_2_get", summary = "Get role", security = {@SecurityRequirement(name = "bearer-key")})
    public Role get(@PathVariable Long id) { return queries.getById(id); }

    @PostMapping
    @Operation(operationId = "role_3_create", summary = "Create role", security = {@SecurityRequirement(name = "bearer-key")})
    public Role create(@Valid @RequestBody RoleRequest req) { return commands.create(req.getName()); }

    @PutMapping("/{id}")
    @Operation(operationId = "role_4_update", summary = "Update role", security = {@SecurityRequirement(name = "bearer-key")})
    public Role update(@PathVariable Long id, @Valid @RequestBody RoleRequest req) {
        return commands.update(id, req.getName());
    }

    @DeleteMapping("/{id}")
    @Operation(operationId = "role_5_delete", summary = "Delete role", security = {@SecurityRequirement(name = "bearer-key")})
    public void delete(@PathVariable Long id) { commands.delete(id); }
}
