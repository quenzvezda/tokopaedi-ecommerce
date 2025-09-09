package com.example.iam.application.role;

import com.example.iam.domain.role.Role;
import com.example.iam.domain.permission.Permission;

import java.util.List;

public interface RoleQueries {
    List<Role> list();
    Role getById(Long id);
    List<Permission> listPermissions(Long roleId);
    List<Permission> listAvailablePermissions(Long roleId);
}
