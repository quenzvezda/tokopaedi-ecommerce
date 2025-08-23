package com.example.iam.application.query;

import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.permission.PermissionRepository;

import java.util.List;

public class ListPermissionsQuery {
    private final PermissionRepository port;
    public ListPermissionsQuery(PermissionRepository port) { this.port = port; }
    public List<Permission> handle() { return port.findAll(); }
}
