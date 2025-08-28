package com.example.iam.application.role;

import com.example.iam.domain.role.Role;
import com.example.iam.domain.role.RoleRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RoleQueryServiceTest {

    RoleRepository repo = mock(RoleRepository.class);
    RoleQueries svc = new RoleQueryService(repo);

    @Test
    void list_delegates() {
        when(repo.findAll()).thenReturn(List.of(new Role(1L,"A")));
        assertThat(svc.list()).extracting(Role::getName).containsExactly("A");
    }

    @Test
    void getById_found() {
        when(repo.findById(1L)).thenReturn(Optional.of(new Role(1L,"A")));
        assertThat(svc.getById(1L).getName()).isEqualTo("A");
    }

    @Test
    void getById_missing_throws() {
        when(repo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> svc.getById(2L));
    }
}
