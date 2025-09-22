package com.example.iam.application.role;

import com.example.iam.domain.common.PageResult;
import com.example.iam.domain.role.Role;
import com.example.iam.domain.permission.Permission;

import java.util.List;

public interface RoleQueries {
    PageResult<Role> list(int page, int size);
    PageResult<Role> search(String q, List<String> sort, int page, int size);
    Role getById(Long id);
    PageResult<Permission> listPermissions(Long roleId, Boolean available, int page, int size);
}
