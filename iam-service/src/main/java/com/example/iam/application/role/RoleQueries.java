package com.example.iam.application.role;

import com.example.iam.domain.role.Role;

import java.util.List;

public interface RoleQueries {
    List<Role> list();
    Role getById(Long id);
}
