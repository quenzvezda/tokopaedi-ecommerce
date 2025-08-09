package com.example.iam.application.command;

import com.example.iam.domain.model.Permission;
import com.example.iam.domain.port.PermissionPort;

public class UpdatePermissionCommand {
    private final PermissionPort port;
    public UpdatePermissionCommand(PermissionPort port) { this.port = port; }

    public Permission handle(Long id, String name, String description) {
        Permission p = port.findById(id).orElseThrow();
        return port.save(p.withName(name).withDescription(description));
    }
}
