package com.example.iam.infrastructure.jpa.repository;

import com.example.iam.infrastructure.jpa.entity.RoleJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JpaRoleRepository extends JpaRepository<RoleJpa, Long> {
    Optional<RoleJpa> findByName(String name);
}
