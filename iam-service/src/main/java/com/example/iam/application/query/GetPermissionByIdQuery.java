package com.example.iam.application.query;

import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.permission.PermissionRepository;

public class GetPermissionByIdQuery {
    private final PermissionRepository port;
    public GetPermissionByIdQuery(PermissionRepository port) { this.port = port; }
    public Permission handle(Long id) { return port.findById(id).orElseThrow(); }
}
