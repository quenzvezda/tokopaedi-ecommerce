package com.example.iam.domain.assignment;

import java.util.Collection;
import java.util.List;

public interface RolePermissionRepository {
    boolean exists(Long roleId, Long permissionId);
    void add(Long roleId, Long permissionId);
    void remove(Long roleId, Long permissionId);
    List<Long> findPermissionIdsByRoleId(Long roleId);
    List<Long> findPermissionIdsByRoleIds(Collection<Long> roleIds);
}
