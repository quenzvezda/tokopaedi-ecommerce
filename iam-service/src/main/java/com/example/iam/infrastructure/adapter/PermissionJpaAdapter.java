package com.example.iam.infrastructure.adapter;

import com.example.iam.domain.model.Permission;
import com.example.iam.domain.port.PermissionPort;
import com.example.iam.infrastructure.persistence.entity.PermissionEntity;
import com.example.iam.infrastructure.persistence.repo.PermissionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissionJpaAdapter implements PermissionPort {
    private final PermissionJpaRepository repo;

    @Override
    public Permission save(Permission p) {
        PermissionEntity e = new PermissionEntity();
        e.setId(p.getId());
        e.setName(p.getName());
        e.setDescription(p.getDescription());
        PermissionEntity s = repo.save(e);
        return new Permission(s.getId(), s.getName(), s.getDescription());
    }

    @Override
    public Optional<Permission> findById(Long id) { return repo.findById(id).map(e -> new Permission(e.getId(), e.getName(), e.getDescription())); }

    @Override
    public Optional<Permission> findByName(String name) { return repo.findByName(name).map(e -> new Permission(e.getId(), e.getName(), e.getDescription())); }

    @Override
    public List<Permission> findAll() { return repo.findAll().stream().map(e -> new Permission(e.getId(), e.getName(), e.getDescription())).toList(); }

    @Override
    public void deleteById(Long id) { repo.deleteById(id); }

    @Override
    public List<Permission> findAllByIds(Collection<Long> ids) { return repo.findAllById(ids).stream().map(e -> new Permission(e.getId(), e.getName(), e.getDescription())).collect(Collectors.toList()); }
}
