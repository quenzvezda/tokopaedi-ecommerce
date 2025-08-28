package com.example.iam.application.assignment;

import com.example.iam.domain.assignment.RolePermissionRepository;
import com.example.iam.domain.entitlement.EntitlementVersionRepository;
import com.example.iam.domain.user.UserRole;
import com.example.iam.domain.user.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class AssignmentCommandServiceTest {

    RolePermissionRepository rolePerm = mock(RolePermissionRepository.class);
    UserRoleRepository userRole = mock(UserRoleRepository.class);
    EntitlementVersionRepository version = mock(EntitlementVersionRepository.class);

    AssignmentCommands svc;

    @BeforeEach
    void setUp() { svc = new AssignmentCommandService(rolePerm, userRole, version); }

    @Test
    void assignPermissionToRole_addsWhenNotExists_andBumpsAllUsersOfRole() {
        Long roleId = 1L, permId = 10L;
        var a = UUID.randomUUID(); var b = UUID.randomUUID();
        when(rolePerm.exists(roleId, permId)).thenReturn(false);
        when(userRole.findByRoleId(roleId)).thenReturn(List.of(
                UserRole.of(a, roleId), UserRole.of(b, roleId)));

        svc.assignPermissionToRole(roleId, permId);

        verify(rolePerm).add(roleId, permId);
        verify(version).bump(a);
        verify(version).bump(b);
    }

    @Test
    void assignPermissionToRole_whenAlreadyExists_doesNotAdd_butStillBumps() {
        Long roleId = 1L, permId = 10L;
        var a = UUID.randomUUID();
        when(rolePerm.exists(roleId, permId)).thenReturn(true);
        when(userRole.findByRoleId(roleId)).thenReturn(List.of(UserRole.of(a, roleId)));

        svc.assignPermissionToRole(roleId, permId);

        verify(rolePerm, never()).add(any(), any());
        verify(version).bump(a);
    }

    @Test
    void removePermissionFromRole_removesWhenExists_andBumps() {
        Long roleId = 1L, permId = 10L;
        var a = UUID.randomUUID();
        when(rolePerm.exists(roleId, permId)).thenReturn(true);
        when(userRole.findByRoleId(roleId)).thenReturn(List.of(UserRole.of(a, roleId)));

        svc.removePermissionFromRole(roleId, permId);

        verify(rolePerm).remove(roleId, permId);
        verify(version).bump(a);
    }

    @Test
    void removePermissionFromRole_whenNotExists_doesNotRemove_butBumps() {
        Long roleId = 1L, permId = 10L;
        var a = UUID.randomUUID();
        when(rolePerm.exists(roleId, permId)).thenReturn(false);
        when(userRole.findByRoleId(roleId)).thenReturn(List.of(UserRole.of(a, roleId)));

        svc.removePermissionFromRole(roleId, permId);

        verify(rolePerm, never()).remove(any(), any());
        verify(version).bump(a);
    }

    @Test
    void assignRoleToUser_addsWhenNotExists_andBumpsAccount() {
        var acc = UUID.randomUUID(); Long roleId = 7L;
        when(userRole.exists(acc, roleId)).thenReturn(false);

        svc.assignRoleToUser(acc, roleId);

        verify(userRole).add(acc, roleId);
        verify(version).bump(acc);
    }

    @Test
    void assignRoleToUser_whenExists_doesNotAdd_butBumps() {
        var acc = UUID.randomUUID(); Long roleId = 7L;
        when(userRole.exists(acc, roleId)).thenReturn(true);

        svc.assignRoleToUser(acc, roleId);

        verify(userRole, never()).add(any(), any());
        verify(version).bump(acc);
    }

    @Test
    void removeRoleFromUser_removesWhenExists_andBumps() {
        var acc = UUID.randomUUID(); Long roleId = 7L;
        when(userRole.exists(acc, roleId)).thenReturn(true);

        svc.removeRoleFromUser(acc, roleId);

        verify(userRole).remove(acc, roleId);
        verify(version).bump(acc);
    }

    @Test
    void removeRoleFromUser_whenNotExists_doesNotRemove_butBumps() {
        var acc = UUID.randomUUID(); Long roleId = 7L;
        when(userRole.exists(acc, roleId)).thenReturn(false);

        svc.removeRoleFromUser(acc, roleId);

        verify(userRole, never()).remove(any(), any());
        verify(version).bump(acc);
    }
}
