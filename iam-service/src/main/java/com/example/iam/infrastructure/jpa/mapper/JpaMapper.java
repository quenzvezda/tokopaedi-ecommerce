package com.example.iam.infrastructure.jpa.mapper;

import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.role.Role;
import com.example.iam.domain.user.UserRole;
import com.example.iam.infrastructure.jpa.entity.PermissionJpa;
import com.example.iam.infrastructure.jpa.entity.RoleJpa;
import com.example.iam.infrastructure.jpa.entity.UserRoleJpa;

public final class JpaMapper {
    private JpaMapper() {}

    // Permission
    public static Permission toDomain(PermissionJpa e) {
        if (e == null) return null;
        return new Permission(e.getId(), e.getName(), e.getDescription());
    }
    public static PermissionJpa toEntity(Permission d) {
        if (d == null) return null;
        PermissionJpa e = new PermissionJpa();
        e.setId(d.getId());
        e.setName(d.getName());
        e.setDescription(d.getDescription());
        return e;
    }

    // Role
    public static Role toDomain(RoleJpa e) {
        if (e == null) return null;
        return new Role(e.getId(), e.getName());
    }
    public static RoleJpa toEntity(Role d) {
        if (d == null) return null;
        RoleJpa e = new RoleJpa();
        e.setId(d.getId());
        e.setName(d.getName());
        return e;
    }

    // UserRole
    public static UserRole toDomain(UserRoleJpa e) {
        if (e == null) return null;
        return new UserRole(e.getId(), e.getAccountId(), e.getRoleId());
    }
}
