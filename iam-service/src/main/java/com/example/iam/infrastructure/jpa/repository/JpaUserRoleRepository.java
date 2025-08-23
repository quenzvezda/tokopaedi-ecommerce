package com.example.iam.infrastructure.jpa.repository;

import com.example.iam.infrastructure.jpa.entity.UserRoleJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaUserRoleRepository extends JpaRepository<UserRoleJpa, Long> {
    Optional<UserRoleJpa> findByAccountIdAndRoleId(UUID accountId, Long roleId);
    List<UserRoleJpa> findAllByAccountId(UUID accountId);
    List<UserRoleJpa> findAllByRoleId(Long roleId);

    @Query("select ur.roleId from UserRoleJpa ur where ur.accountId = ?1")
    List<Long> findRoleIdsByAccountId(UUID accountId);
}
