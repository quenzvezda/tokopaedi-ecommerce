package com.example.iam.application.command;

import com.example.iam.domain.model.Permission;
import com.example.iam.domain.port.PermissionPort;

public class CreatePermissionCommand {
    private final PermissionPort port;
    public CreatePermissionCommand(PermissionPort port) { this.port = port; }
    public Permission handle(String name, String description) { return port.save(Permission.ofNew(name, description)); }
}
