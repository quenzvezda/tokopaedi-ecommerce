package com.example.iam.domain.permission;

import com.example.iam.domain.common.PageResult;
import java.util.*;

public interface PermissionRepository {
    Permission save(Permission p);
    List<Permission> saveAll(Collection<Permission> permissions);
    Optional<Permission> findById(Long id);
    Optional<Permission> findByName(String name);
    List<Permission> findAll();
    PageResult<Permission> findAllPaged(int page, int size);
    PageResult<Permission> search(String q, List<String> sort, int page, int size);
    void deleteById(Long id);
    List<Permission> findAllByIds(Collection<Long> ids);
    List<String> findNamesByIds(Collection<Long> ids);
}

