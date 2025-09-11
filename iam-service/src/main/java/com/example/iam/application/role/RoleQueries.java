package com.example.iam.application.role;

import com.example.iam.domain.common.PageResult;
import com.example.iam.domain.role.Role;
import com.example.iam.domain.permission.Permission;

public interface RoleQueries {
    PageResult<Role> list(int page, int size);
    Role getById(Long id);
    PageResult<Permission> listPermissions(Long roleId, int page, int size);
    PageResult<Permission> listAvailablePermissions(Long roleId, int page, int size);
}
