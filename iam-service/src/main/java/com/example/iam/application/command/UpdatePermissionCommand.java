package com.example.iam.application.command;

import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.permission.PermissionRepository;

public class UpdatePermissionCommand {
    private final PermissionRepository port;
    public UpdatePermissionCommand(PermissionRepository port) { this.port = port; }

    public Permission handle(Long id, String name, String description) {
        Permission p = port.findById(id).orElseThrow();
        return port.save(p.withName(name).withDescription(description));
    }
}
