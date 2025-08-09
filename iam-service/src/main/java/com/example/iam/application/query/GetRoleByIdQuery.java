package com.example.iam.application.query;

import com.example.iam.domain.model.Role;
import com.example.iam.domain.port.RolePort;

public class GetRoleByIdQuery {
    private final RolePort port;
    public GetRoleByIdQuery(RolePort port) { this.port = port; }
    public Role handle(Long id) { return port.findById(id).orElseThrow(); }
}
