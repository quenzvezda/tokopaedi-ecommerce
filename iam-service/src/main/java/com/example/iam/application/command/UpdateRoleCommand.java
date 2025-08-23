package com.example.iam.application.command;

import com.example.iam.domain.role.Role;
import com.example.iam.domain.role.RoleRepository;

public class UpdateRoleCommand {
    private final RoleRepository port;
    public UpdateRoleCommand(RoleRepository port) { this.port = port; }

    public Role handle(Long id, String name) {
        Role r = port.findById(id).orElseThrow();
        return port.save(r.withName(name));
    }
}
