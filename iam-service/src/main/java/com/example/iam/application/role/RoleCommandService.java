package com.example.iam.application.role;

import com.example.iam.domain.role.Role;
import com.example.iam.domain.role.RoleRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RoleCommandService implements RoleCommands {
    private final RoleRepository repo;

    @Override public Role create(String name) { return repo.save(Role.ofNew(name)); }

    @Override
    public Role update(Long id, String name) {
        var r = repo.findById(id).orElseThrow();
        return repo.save(r.withName(name));
    }

    @Override public void delete(Long id) { repo.deleteById(id); }
}
