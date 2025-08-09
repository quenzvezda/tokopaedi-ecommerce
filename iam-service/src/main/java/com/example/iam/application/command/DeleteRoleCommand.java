package com.example.iam.application.command;

import com.example.iam.domain.port.RolePort;

public class DeleteRoleCommand {
    private final RolePort port;
    public DeleteRoleCommand(RolePort port) { this.port = port; }
    public void handle(Long id) { port.deleteById(id); }
}
