package com.example.iam.web;

import com.example.iam.application.command.CreateRoleCommand;
import com.example.iam.application.command.DeleteRoleCommand;
import com.example.iam.application.command.UpdateRoleCommand;
import com.example.iam.application.query.GetRoleByIdQuery;
import com.example.iam.application.query.ListRolesQuery;
import com.example.iam.domain.model.Role;
import com.example.iam.web.dto.RoleRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final CreateRoleCommand create;
    private final UpdateRoleCommand update;
    private final DeleteRoleCommand delete;
    private final ListRolesQuery list;
    private final GetRoleByIdQuery get;

    @GetMapping
    public List<Role> all() { return list.handle(); }

    @GetMapping("/{id}")
    public Role get(@PathVariable("id") Long id) { return get.handle(id); }

    @PostMapping
    public Role create(@Valid @RequestBody RoleRequest req) { return create.handle(req.getName()); }

    @PutMapping("/{id}")
    public Role update(@PathVariable("id") Long id, @Valid @RequestBody RoleRequest req) { return update.handle(id, req.getName()); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) { delete.handle(id); }
}
