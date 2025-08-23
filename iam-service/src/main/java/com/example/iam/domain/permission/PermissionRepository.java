package com.example.iam.domain.permission;

import java.util.*;

public interface PermissionRepository {
    Permission save(Permission p);
    Optional<Permission> findById(Long id);
    Optional<Permission> findByName(String name);
    List<Permission> findAll();
    void deleteById(Long id);
    List<Permission> findAllByIds(Collection<Long> ids);
    List<String> findNamesByIds(Collection<Long> ids);
}
