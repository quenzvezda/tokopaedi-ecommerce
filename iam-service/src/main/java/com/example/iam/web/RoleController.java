package com.example.iam.web;

import com.example.iam.application.role.RoleCommands;
import com.example.iam.application.role.RoleQueries;
import com.example.iam.domain.role.Role;
import com.example.iam.web.dto.RoleRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleCommands commands;
    private final RoleQueries queries;

    @GetMapping
    public List<Role> all() { return queries.list(); }

    @GetMapping("/{id}")
    public Role get(@PathVariable Long id) { return queries.getById(id); }

    @PostMapping
    public Role create(@Valid @RequestBody RoleRequest req) { return commands.create(req.getName()); }

    @PutMapping("/{id}")
    public Role update(@PathVariable Long id, @Valid @RequestBody RoleRequest req) {
        return commands.update(id, req.getName());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { commands.delete(id); }
}
