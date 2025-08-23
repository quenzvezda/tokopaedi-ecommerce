package com.example.iam.web;

import com.example.iam.application.permission.PermissionCommands;
import com.example.iam.application.permission.PermissionQueries;
import com.example.iam.domain.permission.Permission;
import com.example.iam.web.dto.PermissionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionCommands commands;
    private final PermissionQueries queries;

    @GetMapping
    public List<Permission> all() { return queries.list(); }

    @GetMapping("/{id}")
    public Permission get(@PathVariable Long id) { return queries.getById(id); }

    @PostMapping
    public Permission create(@Valid @RequestBody PermissionRequest req) {
        return commands.create(req.getName(), req.getDescription());
    }

    @PutMapping("/{id}")
    public Permission update(@PathVariable Long id, @Valid @RequestBody PermissionRequest req) {
        return commands.update(id, req.getName(), req.getDescription());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { commands.delete(id); }
}
