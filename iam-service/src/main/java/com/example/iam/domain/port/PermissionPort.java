package com.example.iam.domain.port;

import com.example.iam.domain.model.Permission;
import java.util.*;

public interface PermissionPort {
    Permission save(Permission p);
    Optional<Permission> findById(Long id);
    Optional<Permission> findByName(String name);
    List<Permission> findAll();
    void deleteById(Long id);
    List<Permission> findAllByIds(Collection<Long> ids);
    List<String> findNamesByIds(Collection<Long> ids);
}
