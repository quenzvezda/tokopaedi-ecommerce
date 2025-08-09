package com.example.iam.infrastructure.persistence.repo;

import com.example.iam.infrastructure.persistence.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RolePermissionJpaRepository extends JpaRepository<RolePermissionEntity, Long> {
    Optional<RolePermissionEntity> findByRoleIdAndPermissionId(Long roleId, Long permissionId);
    List<RolePermissionEntity> findAllByRoleId(Long roleId);
}
