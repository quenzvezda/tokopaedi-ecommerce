package com.example.iam.infrastructure.adapter;

import com.example.iam.domain.port.RolePermissionPort;
import com.example.iam.infrastructure.persistence.entity.RolePermissionEntity;
import com.example.iam.infrastructure.persistence.repo.RolePermissionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RolePermissionJpaAdapter implements RolePermissionPort {
    private final RolePermissionJpaRepository repo;

    @Override
    public boolean exists(Long roleId, Long permissionId) { return repo.findByRoleIdAndPermissionId(roleId, permissionId).isPresent(); }

    @Override
    public void add(Long roleId, Long permissionId) {
        RolePermissionEntity e = new RolePermissionEntity();
        e.setRoleId(roleId);
        e.setPermissionId(permissionId);
        repo.save(e);
    }

    @Override
    public void remove(Long roleId, Long permissionId) { repo.findByRoleIdAndPermissionId(roleId, permissionId).ifPresent(repo::delete); }

    @Override
    public List<Long> findPermissionIdsByRoleId(Long roleId) { return repo.findAllByRoleId(roleId).stream().map(RolePermissionEntity::getPermissionId).toList(); }

    @Override
    public List<Long> findPermissionIdsByRoleIds(Collection<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return List.of();
        return repo.findPermissionIdsByRoleIds(roleIds);
    }
}
