package com.example.iam.application.permission;

import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.permission.PermissionRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PermissionQueryServiceTest {

    PermissionRepository repo = mock(PermissionRepository.class);
    PermissionQueries svc = new PermissionQueryService(repo);

    @Test
    void list_delegatesToRepo() {
        when(repo.findAll()).thenReturn(List.of(new Permission(1L,"A",null)));
        assertThat(svc.list()).hasSize(1);
    }

    @Test
    void getById_whenMissing_throws() {
        when(repo.findById(9L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> svc.getById(9L));
    }

    @Test
    void getById_whenFound_returns() {
        when(repo.findById(1L)).thenReturn(Optional.of(new Permission(1L,"A",null)));
        assertThat(svc.getById(1L).getName()).isEqualTo("A");
    }
}
