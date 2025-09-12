package com.example.iam.application.permission;

import com.example.iam.domain.common.PageResult;
import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.permission.PermissionRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PermissionQueryService implements PermissionQueries {
    private final PermissionRepository repo;

    @Override public PageResult<Permission> list(int page, int size) { return repo.findAllPaged(page, size); }
    @Override public PageResult<Permission> search(String q, List<String> sort, int page, int size) { return repo.search(q, sort, page, size); }
    @Override public Permission getById(Long id) { return repo.findById(id).orElseThrow(); }
}
