package com.example.iam.infrastructure.persistence.repo;

import com.example.iam.infrastructure.persistence.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, Long> {
    Optional<PermissionEntity> findByName(String name);

    @Query("select p.name from PermissionEntity p where p.id in ?1")
    List<String> findNamesByIds(Collection<Long> ids);
}
