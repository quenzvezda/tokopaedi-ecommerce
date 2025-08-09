package com.example.iam.infrastructure.persistence.repo;

import com.example.iam.infrastructure.persistence.entity.UserEntitlementVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserEntitlementVersionJpaRepository extends JpaRepository<UserEntitlementVersionEntity, Long> {
    Optional<UserEntitlementVersionEntity> findByAccountId(UUID accountId);
}
