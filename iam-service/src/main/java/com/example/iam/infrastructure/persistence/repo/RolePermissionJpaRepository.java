package com.example.iam.infrastructure.persistence.repo;

import com.example.iam.infrastructure.persistence.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RolePermissionJpaRepository extends JpaRepository<RolePermissionEntity, Long> {
    Optional<RolePermissionEntity> findByRoleIdAndPermissionId(Long roleId, Long permissionId);
    List<RolePermissionEntity> findAllByRoleId(Long roleId);

    @Query("select rp.permissionId from RolePermissionEntity rp where rp.roleId in ?1")
    List<Long> findPermissionIdsByRoleIds(Collection<Long> roleIds);
}
