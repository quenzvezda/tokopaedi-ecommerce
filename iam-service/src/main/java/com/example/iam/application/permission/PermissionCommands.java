package com.example.iam.application.permission;

import com.example.iam.domain.permission.Permission;

import java.util.List;

public interface PermissionCommands {
    Permission create(String name, String description);
    List<Permission> createBulk(List<CreatePermission> permissions);
    Permission update(Long id, String name, String description);
    void delete(Long id);

    record CreatePermission(String name, String description) {}
}

