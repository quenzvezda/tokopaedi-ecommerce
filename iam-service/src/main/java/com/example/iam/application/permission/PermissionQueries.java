package com.example.iam.application.permission;

import com.example.iam.domain.common.PageResult;
import com.example.iam.domain.permission.Permission;

import java.util.List;

public interface PermissionQueries {
    PageResult<Permission> list(int page, int size);
    PageResult<Permission> search(String q, List<String> sort, int page, int size);
    Permission getById(Long id);
}
