package com.example.iam.config;

import com.example.iam.application.assignment.AssignmentCommandService;
import com.example.iam.application.assignment.AssignmentCommands;
import com.example.iam.application.entitlement.EntitlementQueries;
import com.example.iam.application.entitlement.EntitlementQueryService;
import com.example.iam.application.permission.PermissionCommandService;
import com.example.iam.application.permission.PermissionCommands;
import com.example.iam.application.permission.PermissionQueries;
import com.example.iam.application.permission.PermissionQueryService;
import com.example.iam.application.role.RoleCommandService;
import com.example.iam.application.role.RoleCommands;
import com.example.iam.application.role.RoleQueries;
import com.example.iam.application.role.RoleQueryService;
import com.example.iam.application.user.UserQueries;
import com.example.iam.application.user.UserQueryService;
import com.example.iam.domain.assignment.RolePermissionRepository;
import com.example.iam.domain.entitlement.EntitlementVersionRepository;
import com.example.iam.domain.permission.PermissionRepository;
import com.example.iam.domain.role.RoleRepository;
import com.example.iam.domain.user.UserRoleRepository;
import com.example.iam.infrastructure.jpa.EntitlementVersionRepositoryImpl;
import com.example.iam.infrastructure.jpa.PermissionRepositoryImpl;
import com.example.iam.infrastructure.jpa.RolePermissionRepositoryImpl;
import com.example.iam.infrastructure.jpa.RoleRepositoryImpl;
import com.example.iam.infrastructure.jpa.UserRoleRepositoryImpl;
import com.example.iam.infrastructure.jpa.repository.JpaPermissionRepository;
import com.example.iam.infrastructure.jpa.repository.JpaRolePermissionRepository;
import com.example.iam.infrastructure.jpa.repository.JpaRoleRepository;
import com.example.iam.infrastructure.jpa.repository.JpaUserEntitlementVersionRepository;
import com.example.iam.infrastructure.jpa.repository.JpaUserRoleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * BeanConfig (factory-only)
 * <p>
 * - Tidak memakai stereotype @Service/@Repository pada adapter/uc.
 * - Semua dependency di-wire melalui factory @Bean agar eksplisit.
 * - Urutan: Infra adapters → Application services per slice (Permission, Role, Assignment, Entitlement, User).
 */
@Configuration
public class BeanConfig {

    // ---------------------------------------------------------------------
    // INFRASTRUCTURE ADAPTERS  (bind ke interface domain.*Repository)
    // ---------------------------------------------------------------------

    /** Adapter JPA → PermissionRepository (domain). */
    @Bean
    public PermissionRepository permissionRepository(JpaPermissionRepository jpaRepo) {
        return new PermissionRepositoryImpl(jpaRepo);
    }

    /** Adapter JPA → RoleRepository (domain). */
    @Bean
    public RoleRepository roleRepository(JpaRoleRepository jpaRepo) {
        return new RoleRepositoryImpl(jpaRepo);
    }

    /** Adapter JPA → UserRoleRepository (domain). */
    @Bean
    public UserRoleRepository userRoleRepository(JpaUserRoleRepository jpaRepo) {
        return new UserRoleRepositoryImpl(jpaRepo);
    }

    /** Adapter JPA → RolePermissionRepository (domain). */
    @Bean
    public RolePermissionRepository rolePermissionRepository(JpaRolePermissionRepository jpaRepo) {
        return new RolePermissionRepositoryImpl(jpaRepo);
    }

    /** Adapter JPA → EntitlementVersionRepository (domain). */
    @Bean
    public EntitlementVersionRepository entitlementVersionRepository(JpaUserEntitlementVersionRepository jpaRepo) {
        return new EntitlementVersionRepositoryImpl(jpaRepo);
    }

    // ---------------------------------------------------------------------
    // APPLICATION: PERMISSION SLICE (CQRS)
    // ---------------------------------------------------------------------

    @Bean
    public PermissionCommands permissionCommands(PermissionRepository repo) {
        return new PermissionCommandService(repo);
    }

    @Bean
    public PermissionQueries permissionQueries(PermissionRepository repo) {
        return new PermissionQueryService(repo);
    }

    // ---------------------------------------------------------------------
    // APPLICATION: ROLE SLICE (CQRS)
    // ---------------------------------------------------------------------

    @Bean
    public RoleCommands roleCommands(RoleRepository repo) {
        return new RoleCommandService(repo);
    }

    @Bean
    public RoleQueries roleQueries(RoleRepository repo) {
        return new RoleQueryService(repo);
    }

    // ---------------------------------------------------------------------
    // APPLICATION: ASSIGNMENT SLICE (role ↔ permission, user ↔ role)
    // ---------------------------------------------------------------------

    @Bean
    public AssignmentCommands assignmentCommands(RolePermissionRepository rolePerm,
                                                 UserRoleRepository userRole,
                                                 EntitlementVersionRepository entVersion) {
        return new AssignmentCommandService(rolePerm, userRole, entVersion);
    }

    // ---------------------------------------------------------------------
    // APPLICATION: ENTITLEMENT SLICE (entitlements + authz check)
    // ---------------------------------------------------------------------

    @Bean
    public EntitlementQueries entitlementQueries(UserRoleRepository userRole,
                                                 RolePermissionRepository rolePerm,
                                                 PermissionRepository permission,
                                                 EntitlementVersionRepository entVersion) {
        return new EntitlementQueryService(userRole, rolePerm, permission, entVersion);
    }

    // ---------------------------------------------------------------------
    // APPLICATION: USER SLICE (queries)
    // ---------------------------------------------------------------------

    @Bean
    public UserQueries userQueries(UserRoleRepository userRole, RoleRepository roleRepo) {
        return new UserQueryService(userRole, roleRepo);
    }
}
