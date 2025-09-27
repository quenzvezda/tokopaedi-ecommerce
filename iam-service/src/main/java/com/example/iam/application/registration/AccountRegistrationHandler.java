package com.example.iam.application.registration;

import com.example.common.messaging.AccountRegisteredEvent;
import com.example.iam.application.assignment.AssignmentCommands;
import com.example.iam.domain.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class AccountRegistrationHandler {

    private static final Logger log = LoggerFactory.getLogger(AccountRegistrationHandler.class);

    private final AssignmentCommands assignmentCommands;
    private final RoleRepository roleRepository;
    private final String defaultRoleName;

    public void handle(AccountRegisteredEvent event) {
        var role = roleRepository.findByName(defaultRoleName)
                .orElseThrow(() -> new IllegalStateException("Default role %s not found".formatted(defaultRoleName)));

        assignmentCommands.assignRoleToUser(event.accountId(), role.getId());
        log.info("Assigned default role {} to account {}", defaultRoleName, event.accountId());
    }
}
