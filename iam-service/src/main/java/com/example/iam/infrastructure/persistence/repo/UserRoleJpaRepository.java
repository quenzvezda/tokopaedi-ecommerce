package com.example.iam.infrastructure.persistence.repo;

import com.example.iam.infrastructure.persistence.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRoleJpaRepository extends JpaRepository<UserRoleEntity, Long> {
    Optional<UserRoleEntity> findByAccountIdAndRoleId(UUID accountId, Long roleId);
    List<UserRoleEntity> findAllByAccountId(UUID accountId);
    List<UserRoleEntity> findAllByRoleId(Long roleId);
}
