package com.example.iam.domain.role;

import com.example.iam.domain.common.PageResult;
import java.util.*;

public interface RoleRepository {
    Role save(Role r);
    Optional<Role> findById(Long id);
    Optional<Role> findByName(String name);
    List<Role> findAll();
    PageResult<Role> findAllPaged(int page, int size);
    PageResult<Role> search(String q, List<String> sort, int page, int size);
    void deleteById(Long id);
}
