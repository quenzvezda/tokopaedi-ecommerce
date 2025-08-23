package com.example.iam.application.role;

import com.example.iam.domain.role.Role;
import com.example.iam.domain.role.RoleRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RoleQueryService implements RoleQueries {
    private final RoleRepository repo;

    @Override public List<Role> list() { return repo.findAll(); }
    @Override public Role getById(Long id) { return repo.findById(id).orElseThrow(); }
}
