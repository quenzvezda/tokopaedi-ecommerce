package com.example.iam.application.permission;

import com.example.iam.domain.permission.Permission;

public interface PermissionCommands {
    Permission create(String name, String description);
    Permission update(Long id, String name, String description);
    void delete(Long id);
}
