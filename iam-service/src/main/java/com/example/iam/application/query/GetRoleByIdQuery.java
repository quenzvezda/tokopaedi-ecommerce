package com.example.iam.application.query;

import com.example.iam.domain.role.Role;
import com.example.iam.domain.role.RoleRepository;

public class GetRoleByIdQuery {
    private final RoleRepository port;
    public GetRoleByIdQuery(RoleRepository port) { this.port = port; }
    public Role handle(Long id) { return port.findById(id).orElseThrow(); }
}
