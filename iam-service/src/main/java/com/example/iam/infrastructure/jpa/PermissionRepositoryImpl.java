package com.example.iam.infrastructure.jpa;

import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.permission.PermissionRepository;
import com.example.iam.infrastructure.jpa.mapper.JpaMapper;
import com.example.iam.infrastructure.jpa.repository.JpaPermissionRepository;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PermissionRepositoryImpl implements PermissionRepository {
    private final JpaPermissionRepository repo;

    @Override
    public Permission save(Permission p) {
        var saved = repo.save(JpaMapper.toEntity(p));
        return JpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return repo.findById(id).map(JpaMapper::toDomain);
    }

    @Override
    public Optional<Permission> findByName(String name) {
        return repo.findByName(name).map(JpaMapper::toDomain);
    }

    @Override
    public List<Permission> findAll() {
        return repo.findAll().stream().map(JpaMapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<Permission> findAllByIds(Collection<Long> ids) {
        return repo.findAllById(ids).stream().map(JpaMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<String> findNamesByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return repo.findNamesByIds(ids);
    }
}
