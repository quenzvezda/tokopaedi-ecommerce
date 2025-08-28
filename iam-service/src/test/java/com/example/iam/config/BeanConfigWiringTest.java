package com.example.iam.config;

import com.example.iam.application.assignment.AssignmentCommands;
import com.example.iam.application.entitlement.EntitlementQueries;
import com.example.iam.application.permission.PermissionCommands;
import com.example.iam.application.permission.PermissionQueries;
import com.example.iam.application.role.RoleCommands;
import com.example.iam.application.role.RoleQueries;
import com.example.iam.application.user.UserQueries;
import com.example.iam.domain.assignment.RolePermissionRepository;
import com.example.iam.domain.entitlement.EntitlementVersionRepository;
import com.example.iam.domain.permission.PermissionRepository;
import com.example.iam.domain.role.RoleRepository;
import com.example.iam.domain.user.UserRoleRepository;
import com.example.iam.infrastructure.jpa.repository.JpaPermissionRepository;
import com.example.iam.infrastructure.jpa.repository.JpaRolePermissionRepository;
import com.example.iam.infrastructure.jpa.repository.JpaRoleRepository;
import com.example.iam.infrastructure.jpa.repository.JpaUserEntitlementVersionRepository;
import com.example.iam.infrastructure.jpa.repository.JpaUserRoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringJUnitConfig
@ActiveProfiles("wiring") // Override default profile ("test")
@ContextConfiguration(classes = BeanConfig.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class BeanConfigWiringTest {

	// Mock repositori JPA yang dipakai factory methods
	@MockBean
	JpaPermissionRepository jpaPermissionRepository;
	@MockBean
	JpaRoleRepository jpaRoleRepository;
	@MockBean
	JpaUserRoleRepository jpaUserRoleRepository;
	@MockBean
	JpaRolePermissionRepository jpaRolePermissionRepository;
	@MockBean
	JpaUserEntitlementVersionRepository jpaUserEntitlementVersionRepository;

	// Bean hasil pabrik di-inject via konstruktor (tanpa @Autowired)
	private final PermissionRepository permissionRepository;
	private final RoleRepository roleRepository;
	private final UserRoleRepository userRoleRepository;
	private final RolePermissionRepository rolePermissionRepository;
	private final EntitlementVersionRepository entitlementVersionRepository;

	private final PermissionCommands permissionCommands;
	private final PermissionQueries permissionQueries;
	private final RoleCommands roleCommands;
	private final RoleQueries roleQueries;
	private final AssignmentCommands assignmentCommands;
	private final EntitlementQueries entitlementQueries;
	private final UserQueries userQueries;

	BeanConfigWiringTest(
			PermissionRepository permissionRepository,
			RoleRepository roleRepository,
			UserRoleRepository userRoleRepository,
			RolePermissionRepository rolePermissionRepository,
			EntitlementVersionRepository entitlementVersionRepository,
			PermissionCommands permissionCommands,
			PermissionQueries permissionQueries,
			RoleCommands roleCommands,
			RoleQueries roleQueries,
			AssignmentCommands assignmentCommands,
			EntitlementQueries entitlementQueries,
			UserQueries userQueries
	) {
		this.permissionRepository = permissionRepository;
		this.roleRepository = roleRepository;
		this.userRoleRepository = userRoleRepository;
		this.rolePermissionRepository = rolePermissionRepository;
		this.entitlementVersionRepository = entitlementVersionRepository;
		this.permissionCommands = permissionCommands;
		this.permissionQueries = permissionQueries;
		this.roleCommands = roleCommands;
		this.roleQueries = roleQueries;
		this.assignmentCommands = assignmentCommands;
		this.entitlementQueries = entitlementQueries;
		this.userQueries = userQueries;
	}

	@Test
	void beans_arePresent() {
		assertThat(permissionRepository).isNotNull();
		assertThat(roleRepository).isNotNull();
		assertThat(userRoleRepository).isNotNull();
		assertThat(rolePermissionRepository).isNotNull();
		assertThat(entitlementVersionRepository).isNotNull();

		assertThat(permissionCommands).isNotNull();
		assertThat(permissionQueries).isNotNull();
		assertThat(roleCommands).isNotNull();
		assertThat(roleQueries).isNotNull();
		assertThat(assignmentCommands).isNotNull();
		assertThat(entitlementQueries).isNotNull();
		assertThat(userQueries).isNotNull();
	}
}

