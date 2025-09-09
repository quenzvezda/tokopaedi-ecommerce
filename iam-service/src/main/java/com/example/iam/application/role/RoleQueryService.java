package com.example.iam.application.role;

import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.permission.PermissionRepository;
import com.example.iam.domain.role.Role;
import com.example.iam.domain.role.RoleRepository;
import com.example.iam.domain.assignment.RolePermissionRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RoleQueryService implements RoleQueries {
    private final RoleRepository roleRepo;
    private final PermissionRepository permissionRepo;
    private final RolePermissionRepository rolePermRepo;

    @Override public List<Role> list() { return roleRepo.findAll(); }
    @Override public Role getById(Long id) { return roleRepo.findById(id).orElseThrow(); }

    @Override
    public List<Permission> listPermissions(Long roleId) {
        var ids = rolePermRepo.findPermissionIdsByRoleId(roleId);
        return permissionRepo.findAllByIds(ids);
    }

    @Override
    public List<Permission> listAvailablePermissions(Long roleId) {
        var assigned = rolePermRepo.findPermissionIdsByRoleId(roleId);
        var assignedSet = new java.util.HashSet<>(assigned);
        return permissionRepo.findAll().stream()
                .filter(p -> !assignedSet.contains(p.getId()))
                .toList();
    }
}
