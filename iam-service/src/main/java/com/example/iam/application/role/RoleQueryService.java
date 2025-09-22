package com.example.iam.application.role;

import com.example.iam.domain.assignment.RolePermissionRepository;
import com.example.iam.domain.common.PageResult;
import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.permission.PermissionRepository;
import com.example.iam.domain.role.Role;
import com.example.iam.domain.role.RoleRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RoleQueryService implements RoleQueries {
    private final RoleRepository roleRepo;
    private final PermissionRepository permissionRepo;
    private final RolePermissionRepository rolePermRepo;

    @Override public PageResult<Role> list(int page, int size) { return roleRepo.findAllPaged(page, size); }
    @Override public PageResult<Role> search(String q, List<String> sort, int page, int size) { return roleRepo.search(q, sort, page, size); }
    @Override public Role getById(Long id) { return roleRepo.findById(id).orElseThrow(); }

    @Override
    public PageResult<Permission> listPermissions(Long roleId, Boolean available, int page, int size) {
        if (available == null) {
            return slice(permissionRepo.findAll(), page, size);
        }

        var assignedIds = rolePermRepo.findPermissionIdsByRoleId(roleId);
        if (Boolean.TRUE.equals(available)) {
            var assignedSet = new java.util.HashSet<>(assignedIds);
            var all = permissionRepo.findAll().stream()
                    .filter(p -> !assignedSet.contains(p.getId()))
                    .toList();
            return slice(all, page, size);
        }

        var assigned = permissionRepo.findAllByIds(assignedIds);
        return slice(assigned, page, size);
    }

    private static <T> PageResult<T> slice(List<T> all, int page, int size) {
        int p = Math.max(0, page);
        int s = Math.max(1, size);
        int from = Math.min(p * s, all.size());
        int to = Math.min(from + s, all.size());
        var content = all.subList(from, to);
        long total = all.size();
        int totalPages = (int) Math.max(1, Math.ceil((double) total / (double) s));
        return new PageResult<>(content, p, s, total, totalPages);
    }
}
