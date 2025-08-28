package com.example.iam.infrastructure.jpa;

import com.example.iam.domain.assignment.RolePermissionRepository;
import com.example.iam.infrastructure.jpa.entity.RolePermissionJpa;
import com.example.iam.infrastructure.jpa.repository.JpaRolePermissionRepository;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class RolePermissionRepositoryImpl implements RolePermissionRepository {
    private final JpaRolePermissionRepository repo;

    @Override
    public boolean exists(Long roleId, Long permissionId) {
        return repo.findByRoleIdAndPermissionId(roleId, permissionId).isPresent();
    }

    @Override
    public void add(Long roleId, Long permissionId) {
        RolePermissionJpa e = new RolePermissionJpa();
        e.setRoleId(roleId);
        e.setPermissionId(permissionId);
        repo.save(e);
    }

    @Override
    public void remove(Long roleId, Long permissionId) {
        repo.findByRoleIdAndPermissionId(roleId, permissionId).ifPresent(repo::delete);
    }

    @Override
    public List<Long> findPermissionIdsByRoleId(Long roleId) {
        return repo.findAllByRoleId(roleId).stream().map(RolePermissionJpa::getPermissionId).toList();
    }

    @Override
    public List<Long> findPermissionIdsByRoleIds(Collection<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return List.of();
        return repo.findPermissionIdsByRoleIds(roleIds).stream().distinct().toList();
    }
}
