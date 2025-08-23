package com.example.iam.infrastructure.jpa;

import com.example.iam.domain.user.UserRole;
import com.example.iam.domain.user.UserRoleRepository;
import com.example.iam.infrastructure.jpa.entity.UserRoleJpa;
import com.example.iam.infrastructure.jpa.mapper.JpaMapper;
import com.example.iam.infrastructure.jpa.repository.JpaUserRoleRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class UserRoleRepositoryImpl implements UserRoleRepository {
    private final JpaUserRoleRepository repo;

    @Override
    public boolean exists(UUID accountId, Long roleId) {
        return repo.findByAccountIdAndRoleId(accountId, roleId).isPresent();
    }

    @Override
    public void add(UUID accountId, Long roleId) {
        UserRoleJpa e = new UserRoleJpa();
        e.setAccountId(accountId);
        e.setRoleId(roleId);
        repo.save(e);
    }

    @Override
    public void remove(UUID accountId, Long roleId) {
        repo.findByAccountIdAndRoleId(accountId, roleId).ifPresent(repo::delete);
    }

    @Override
    public List<UserRole> findByAccountId(UUID accountId) {
        return repo.findAllByAccountId(accountId).stream().map(JpaMapper::toDomain).toList();
    }

    @Override
    public List<UserRole> findByRoleId(Long roleId) {
        return repo.findAllByRoleId(roleId).stream().map(JpaMapper::toDomain).toList();
    }

    @Override
    public List<Long> findRoleIdsByAccountId(UUID accountId) {
        return repo.findRoleIdsByAccountId(accountId);
    }
}
