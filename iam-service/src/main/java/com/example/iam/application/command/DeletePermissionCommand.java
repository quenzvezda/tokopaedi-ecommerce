package com.example.iam.application.command;

import com.example.iam.domain.permission.PermissionRepository;

public class DeletePermissionCommand {
    private final PermissionRepository port;
    public DeletePermissionCommand(PermissionRepository port) { this.port = port; }
    public void handle(Long id) { port.deleteById(id); }
}
