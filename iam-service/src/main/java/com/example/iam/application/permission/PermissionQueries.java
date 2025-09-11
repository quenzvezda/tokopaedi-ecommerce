package com.example.iam.application.permission;

import com.example.iam.domain.common.PageResult;
import com.example.iam.domain.permission.Permission;

public interface PermissionQueries {
    PageResult<Permission> list(int page, int size);
    Permission getById(Long id);
}
