package com.example.iam.application.command;

import com.example.iam.domain.role.RoleRepository;

public class DeleteRoleCommand {
    private final RoleRepository port;
    public DeleteRoleCommand(RoleRepository port) { this.port = port; }
    public void handle(Long id) { port.deleteById(id); }
}
