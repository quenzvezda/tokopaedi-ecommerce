package com.example.iam.application.command;

import com.example.iam.domain.role.Role;
import com.example.iam.domain.role.RoleRepository;

public class CreateRoleCommand {
    private final RoleRepository port;
    public CreateRoleCommand(RoleRepository port) { this.port = port; }
    public Role handle(String name) { return port.save(Role.ofNew(name)); }
}
