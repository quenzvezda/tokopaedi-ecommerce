package com.example.iam.infrastructure.jpa.repository;

import com.example.iam.infrastructure.jpa.entity.PermissionJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface JpaPermissionRepository extends JpaRepository<PermissionJpa, Long>, JpaSpecificationExecutor<PermissionJpa> {
    Optional<PermissionJpa> findByName(String name);

    @Query("select p.name from PermissionJpa p where p.id in ?1")
    List<String> findNamesByIds(Collection<Long> ids);
}
