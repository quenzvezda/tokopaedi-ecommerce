package com.example.iam.application.permission;

import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.permission.PermissionRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PermissionQueryService implements PermissionQueries {
    private final PermissionRepository repo;

    @Override public List<Permission> list() { return repo.findAll(); }
    @Override public Permission getById(Long id) { return repo.findById(id).orElseThrow(); }
}
