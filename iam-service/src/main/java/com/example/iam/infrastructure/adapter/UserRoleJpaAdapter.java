package com.example.iam.infrastructure.adapter;

import com.example.iam.domain.model.UserRole;
import com.example.iam.domain.port.UserRolePort;
import com.example.iam.infrastructure.persistence.entity.UserRoleEntity;
import com.example.iam.infrastructure.persistence.repo.UserRoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRoleJpaAdapter implements UserRolePort {
    private final UserRoleJpaRepository repo;

    @Override
    public boolean exists(UUID accountId, Long roleId) { return repo.findByAccountIdAndRoleId(accountId, roleId).isPresent(); }

    @Override
    public void add(UUID accountId, Long roleId) {
        UserRoleEntity e = new UserRoleEntity();
        e.setAccountId(accountId);
        e.setRoleId(roleId);
        repo.save(e);
    }

    @Override
    public void remove(UUID accountId, Long roleId) { repo.findByAccountIdAndRoleId(accountId, roleId).ifPresent(repo::delete); }

    @Override
    public List<UserRole> findByAccountId(UUID accountId) { return repo.findAllByAccountId(accountId).stream().map(e -> new UserRole(e.getId(), e.getAccountId(), e.getRoleId())).toList(); }

    @Override
    public List<UserRole> findByRoleId(Long roleId) { return repo.findAllByRoleId(roleId).stream().map(e -> new UserRole(e.getId(), e.getAccountId(), e.getRoleId())).toList(); }

    @Override
    public List<Long> findRoleIdsByAccountId(UUID accountId) {
        return repo.findRoleIdsByAccountId(accountId);
    }
}
