package com.example.iam.application.command;

import com.example.iam.domain.model.Role;
import com.example.iam.domain.port.RolePort;

public class UpdateRoleCommand {
    private final RolePort port;
    public UpdateRoleCommand(RolePort port) { this.port = port; }

    public Role handle(Long id, String name) {
        Role r = port.findById(id).orElseThrow();
        return port.save(r.withName(name));
    }
}
