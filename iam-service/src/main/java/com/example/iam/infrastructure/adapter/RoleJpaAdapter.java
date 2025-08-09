package com.example.iam.infrastructure.adapter;

import com.example.iam.domain.model.Role;
import com.example.iam.domain.port.RolePort;
import com.example.iam.infrastructure.persistence.entity.RoleEntity;
import com.example.iam.infrastructure.persistence.repo.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleJpaAdapter implements RolePort {
    private final RoleJpaRepository repo;

    @Override
    public Role save(Role r) {
        RoleEntity e = new RoleEntity();
        e.setId(r.getId());
        e.setName(r.getName());
        RoleEntity s = repo.save(e);
        return new Role(s.getId(), s.getName());
    }

    @Override
    public Optional<Role> findById(Long id) { return repo.findById(id).map(e -> new Role(e.getId(), e.getName())); }

    @Override
    public Optional<Role> findByName(String name) { return repo.findByName(name).map(e -> new Role(e.getId(), e.getName())); }

    @Override
    public List<Role> findAll() { return repo.findAll().stream().map(e -> new Role(e.getId(), e.getName())).toList(); }

    @Override
    public void deleteById(Long id) { repo.deleteById(id); }
}
