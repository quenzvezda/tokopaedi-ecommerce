package com.example.iam.infrastructure.jpa;

import com.example.iam.domain.entitlement.EntitlementVersionRepository;
import com.example.iam.infrastructure.jpa.entity.UserEntitlementVersionJpa;
import com.example.iam.infrastructure.jpa.repository.JpaUserEntitlementVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
public class EntitlementVersionRepositoryImpl implements EntitlementVersionRepository {
    private final JpaUserEntitlementVersionRepository repo;

    @Override
    @Transactional
    public int getOrInit(UUID accountId) {
        return repo.findByAccountId(accountId)
                .map(UserEntitlementVersionJpa::getVersion)
                .orElseGet(() -> {
                    var v = new UserEntitlementVersionJpa();
                    v.setAccountId(accountId);
                    v.setVersion(1);
                    repo.save(v);
                    return 1;
                });
    }

    @Override
    @Transactional
    public void bump(UUID accountId) {
        var v = repo.findByAccountId(accountId).orElseGet(() -> {
            var nv = new UserEntitlementVersionJpa();
            nv.setAccountId(accountId);
            nv.setVersion(1);
            return repo.save(nv);
        });
        v.setVersion(v.getVersion() + 1);
        repo.save(v);
    }
}
