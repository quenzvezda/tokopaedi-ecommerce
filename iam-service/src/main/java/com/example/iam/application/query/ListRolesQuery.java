package com.example.iam.application.query;

import com.example.iam.domain.model.Role;
import com.example.iam.domain.port.RolePort;

import java.util.List;

public class ListRolesQuery {
    private final RolePort port;
    public ListRolesQuery(RolePort port) { this.port = port; }
    public List<Role> handle() { return port.findAll(); }
}
