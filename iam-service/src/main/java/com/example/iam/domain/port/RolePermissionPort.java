package com.example.iam.domain.port;

import java.util.List;

public interface RolePermissionPort {
    boolean exists(Long roleId, Long permissionId);
    void add(Long roleId, Long permissionId);
    void remove(Long roleId, Long permissionId);
    List<Long> findPermissionIdsByRoleId(Long roleId);
}
