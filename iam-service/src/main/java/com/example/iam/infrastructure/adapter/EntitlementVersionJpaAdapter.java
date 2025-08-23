package com.example.iam.infrastructure.adapter;

import com.example.iam.domain.port.EntitlementVersionPort;
import com.example.iam.infrastructure.persistence.entity.UserEntitlementVersionEntity;
import com.example.iam.infrastructure.persistence.repo.UserEntitlementVersionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EntitlementVersionJpaAdapter implements EntitlementVersionPort {
    private final UserEntitlementVersionJpaRepository repo;

    @Override
    @Transactional
    public int getOrInit(UUID accountId) {
        return repo.findByAccountId(accountId).map(UserEntitlementVersionEntity::getVersion).orElseGet(() -> {
            UserEntitlementVersionEntity v = new UserEntitlementVersionEntity();
            v.setAccountId(accountId);
            v.setVersion(1);
            repo.save(v);
            return 1;
        });
    }

    @Override
    @Transactional
    public void bump(UUID accountId) {
        UserEntitlementVersionEntity v = repo.findByAccountId(accountId).orElseGet(() -> {
            UserEntitlementVersionEntity nv = new UserEntitlementVersionEntity();
            nv.setAccountId(accountId);
            nv.setVersion(1);
            return repo.save(nv);
        });
        v.setVersion(v.getVersion() + 1);
        repo.save(v);
    }
}
