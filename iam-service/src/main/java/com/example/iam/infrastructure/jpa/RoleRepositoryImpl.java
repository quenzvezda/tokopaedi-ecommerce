package com.example.iam.infrastructure.jpa;

import com.example.iam.domain.role.Role;
import com.example.iam.domain.role.RoleRepository;
import com.example.iam.infrastructure.jpa.mapper.JpaMapper;
import com.example.iam.infrastructure.jpa.repository.JpaRoleRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {
    private final JpaRoleRepository repo;

    @Override
    public Role save(Role r) {
        var saved = repo.save(JpaMapper.toEntity(r));
        return JpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Role> findById(Long id) {
        return repo.findById(id).map(JpaMapper::toDomain);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return repo.findByName(name).map(JpaMapper::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return repo.findAll().stream().map(JpaMapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
