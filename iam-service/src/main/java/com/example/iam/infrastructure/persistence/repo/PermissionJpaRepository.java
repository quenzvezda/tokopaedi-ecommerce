package com.example.iam.infrastructure.persistence.repo;

import com.example.iam.infrastructure.persistence.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, Long> {
    Optional<PermissionEntity> findByName(String name);
}
