package com.example.iam.application.role;

import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.permission.PermissionRepository;
import com.example.iam.domain.role.Role;
import com.example.iam.domain.role.RoleRepository;
import com.example.iam.domain.assignment.RolePermissionRepository;
import com.example.iam.domain.common.PageResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RoleQueryServiceTest {

    RoleRepository roleRepo = mock(RoleRepository.class);
    PermissionRepository permRepo = mock(PermissionRepository.class);
    RolePermissionRepository rolePermRepo = mock(RolePermissionRepository.class);
    RoleQueries svc = new RoleQueryService(roleRepo, permRepo, rolePermRepo);

    @Test
    void list_delegates() {
        when(roleRepo.findAllPaged(0, 20)).thenReturn(PageResult.of(List.of(new Role(1L,"A")), 0, 20, 1));
        assertThat(svc.list(0,20).content()).extracting(Role::getName).containsExactly("A");
    }

    @Test
    void getById_found() {
        when(roleRepo.findById(1L)).thenReturn(Optional.of(new Role(1L,"A")));
        assertThat(svc.getById(1L).getName()).isEqualTo("A");
    }

    @Test
    void getById_missing_throws() {
        when(roleRepo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> svc.getById(2L));
    }

    @Test
    void listPermissions_ok() {
        when(rolePermRepo.findPermissionIdsByRoleId(1L)).thenReturn(List.of(1L));
        when(permRepo.findAllByIds(List.of(1L))).thenReturn(List.of(new Permission(1L, "READ", null)));
        assertThat(svc.listPermissions(1L,0,20).content()).extracting(Permission::getName).containsExactly("READ");
    }

    @Test
    void listAvailablePermissions_ok() {
        when(rolePermRepo.findPermissionIdsByRoleId(1L)).thenReturn(List.of(1L));
        when(permRepo.findAll()).thenReturn(List.of(
                new Permission(1L, "READ", null),
                new Permission(2L, "WRITE", null)
        ));
        assertThat(svc.listAvailablePermissions(1L,0,20).content()).extracting(Permission::getName).containsExactly("WRITE");
    }
}
