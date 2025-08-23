package com.example.iam.infrastructure.jpa.repository;

import com.example.iam.infrastructure.jpa.entity.RolePermissionJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface JpaRolePermissionRepository extends JpaRepository<RolePermissionJpa, Long> {
    Optional<RolePermissionJpa> findByRoleIdAndPermissionId(Long roleId, Long permissionId);
    List<RolePermissionJpa> findAllByRoleId(Long roleId);

    @Query("select rp.permissionId from RolePermissionJpa rp where rp.roleId in ?1")
    List<Long> findPermissionIdsByRoleIds(Collection<Long> roleIds);
}
