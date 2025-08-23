package com.example.iam.application.command;

import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.permission.PermissionRepository;

public class CreatePermissionCommand {
    private final PermissionRepository port;
    public CreatePermissionCommand(PermissionRepository port) { this.port = port; }
    public Permission handle(String name, String description) { return port.save(Permission.ofNew(name, description)); }
}
