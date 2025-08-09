package com.example.iam.application.query;

import com.example.iam.domain.model.Permission;
import com.example.iam.domain.port.PermissionPort;

public class GetPermissionByIdQuery {
    private final PermissionPort port;
    public GetPermissionByIdQuery(PermissionPort port) { this.port = port; }
    public Permission handle(Long id) { return port.findById(id).orElseThrow(); }
}
