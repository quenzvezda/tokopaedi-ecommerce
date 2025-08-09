package com.example.iam.application.command;

import com.example.iam.domain.port.PermissionPort;

public class DeletePermissionCommand {
    private final PermissionPort port;
    public DeletePermissionCommand(PermissionPort port) { this.port = port; }
    public void handle(Long id) { port.deleteById(id); }
}
