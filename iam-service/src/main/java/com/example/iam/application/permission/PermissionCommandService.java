package com.example.iam.application.permission;

import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.permission.PermissionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PermissionCommandService implements PermissionCommands {
    private final PermissionRepository repo;

    @Override
    public Permission create(String name, String description) {
        return repo.save(Permission.ofNew(name, description));
    }

    @Override
    public Permission update(Long id, String name, String description) {
        var p = repo.findById(id).orElseThrow();
        return repo.save(p.withName(name).withDescription(description));
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
