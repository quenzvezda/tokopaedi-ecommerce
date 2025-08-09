package com.example.iam.config;

import com.example.iam.application.command.*;
import com.example.iam.application.query.*;
import com.example.iam.domain.port.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean public CreatePermissionCommand createPermissionCommand(PermissionPort p) { return new CreatePermissionCommand(p); }
    @Bean public UpdatePermissionCommand updatePermissionCommand(PermissionPort p) { return new UpdatePermissionCommand(p); }
    @Bean public DeletePermissionCommand deletePermissionCommand(PermissionPort p) { return new DeletePermissionCommand(p); }
    @Bean public ListPermissionsQuery listPermissionsQuery(PermissionPort p) { return new ListPermissionsQuery(p); }
    @Bean public GetPermissionByIdQuery getPermissionByIdQuery(PermissionPort p) { return new GetPermissionByIdQuery(p); }

    @Bean public CreateRoleCommand createRoleCommand(RolePort r) { return new CreateRoleCommand(r); }
    @Bean public UpdateRoleCommand updateRoleCommand(RolePort r) { return new UpdateRoleCommand(r); }
    @Bean public DeleteRoleCommand deleteRoleCommand(RolePort r) { return new DeleteRoleCommand(r); }
    @Bean public ListRolesQuery listRolesQuery(RolePort r) { return new ListRolesQuery(r); }
    @Bean public GetRoleByIdQuery getRoleByIdQuery(RolePort r) { return new GetRoleByIdQuery(r); }

    @Bean public AssignPermissionToRoleCommand assignPermissionToRoleCommand(RolePermissionPort rp, UserRolePort ur, EntitlementVersionPort ev) { return new AssignPermissionToRoleCommand(rp, ur, ev); }
    @Bean public RemovePermissionFromRoleCommand removePermissionFromRoleCommand(RolePermissionPort rp, UserRolePort ur, EntitlementVersionPort ev) { return new RemovePermissionFromRoleCommand(rp, ur, ev); }
    @Bean public AssignRoleToUserCommand assignRoleToUserCommand(UserRolePort ur, EntitlementVersionPort ev) { return new AssignRoleToUserCommand(ur, ev); }
    @Bean public RemoveRoleFromUserCommand removeRoleFromUserCommand(UserRolePort ur, EntitlementVersionPort ev) { return new RemoveRoleFromUserCommand(ur, ev); }

    @Bean public GetEntitlementsQuery getEntitlementsQuery(UserRolePort ur, RolePermissionPort rp, PermissionPort p, EntitlementVersionPort ev) { return new GetEntitlementsQuery(ur, rp, p, ev); }
}
