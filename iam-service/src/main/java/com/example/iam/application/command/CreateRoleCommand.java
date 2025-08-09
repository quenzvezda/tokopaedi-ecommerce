package com.example.iam.application.command;

import com.example.iam.domain.model.Role;
import com.example.iam.domain.port.RolePort;

public class CreateRoleCommand {
    private final RolePort port;
    public CreateRoleCommand(RolePort port) { this.port = port; }
    public Role handle(String name) { return port.save(Role.ofNew(name)); }
}
