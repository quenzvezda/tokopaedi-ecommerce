package com.example.iam.application.role;

import com.example.iam.domain.role.Role;

public interface RoleCommands {
    Role create(String name);
    Role update(Long id, String name);
    void delete(Long id);
}
