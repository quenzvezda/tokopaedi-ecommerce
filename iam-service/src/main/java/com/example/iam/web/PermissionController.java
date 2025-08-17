package com.example.iam.web;

import com.example.iam.application.command.CreatePermissionCommand;
import com.example.iam.application.command.DeletePermissionCommand;
import com.example.iam.application.command.UpdatePermissionCommand;
import com.example.iam.application.query.GetPermissionByIdQuery;
import com.example.iam.application.query.ListPermissionsQuery;
import com.example.iam.domain.model.Permission;
import com.example.iam.web.dto.PermissionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final CreatePermissionCommand create;
    private final UpdatePermissionCommand update;
    private final DeletePermissionCommand delete;
    private final ListPermissionsQuery list;
    private final GetPermissionByIdQuery get;

    @GetMapping
    public List<Permission> all() { return list.handle(); }

    @GetMapping("/{id}")
    public Permission get(@PathVariable("id") Long id) { return get.handle(id); }

    @PostMapping
    public Permission create(@Valid @RequestBody PermissionRequest req) { return create.handle(req.getName(), req.getDescription()); }

    @PutMapping("/{id}")
    public Permission update(@PathVariable("id") Long id, @Valid @RequestBody PermissionRequest req) { return update.handle(id, req.getName(), req.getDescription()); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) { delete.handle(id); }
}
