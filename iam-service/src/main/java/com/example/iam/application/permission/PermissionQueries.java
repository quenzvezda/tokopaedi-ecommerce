package com.example.iam.application.permission;

import com.example.iam.domain.permission.Permission;

import java.util.List;

public interface PermissionQueries {
    List<Permission> list();
    Permission getById(Long id);
}
