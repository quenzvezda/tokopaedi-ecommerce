package com.example.iam.infrastructure.jpa.repository;

import com.example.iam.infrastructure.jpa.entity.UserEntitlementVersionJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface JpaUserEntitlementVersionRepository extends JpaRepository<UserEntitlementVersionJpa, Long> {
    Optional<UserEntitlementVersionJpa> findByAccountId(UUID accountId);
}
