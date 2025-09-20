package com.example.iam.application.permission;

import com.example.iam.application.permission.PermissionCommands.CreatePermission;
import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.permission.PermissionRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PermissionCommandService implements PermissionCommands {
    private final PermissionRepository repo;

    @Override
    public Permission create(String name, String description) {
        return repo.save(Permission.ofNew(name, description));
    }

    @Override
    public List<Permission> createBulk(List<CreatePermission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            throw new IllegalArgumentException("permissions must not be empty");
        }
        var toPersist = permissions.stream()
                .map(p -> Permission.ofNew(p.name(), p.description()))
                .toList();
        return repo.saveAll(toPersist);
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

