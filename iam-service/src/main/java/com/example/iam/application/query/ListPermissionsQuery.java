package com.example.iam.application.query;

import com.example.iam.domain.model.Permission;
import com.example.iam.domain.port.PermissionPort;

import java.util.List;

public class ListPermissionsQuery {
    private final PermissionPort port;
    public ListPermissionsQuery(PermissionPort port) { this.port = port; }
    public List<Permission> handle() { return port.findAll(); }
}
