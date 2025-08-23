package com.example.iam.application.query;

import com.example.iam.domain.role.Role;
import com.example.iam.domain.role.RoleRepository;

import java.util.List;

public class ListRolesQuery {
    private final RoleRepository port;
    public ListRolesQuery(RoleRepository port) { this.port = port; }
    public List<Role> handle() { return port.findAll(); }
}
