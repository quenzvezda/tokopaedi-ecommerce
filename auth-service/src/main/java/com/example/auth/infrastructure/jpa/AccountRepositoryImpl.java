package com.example.auth.infrastructure.jpa;

import com.example.auth.domain.account.Account;
import com.example.auth.domain.account.AccountRepository;
import com.example.auth.domain.common.PageResult;
import com.example.auth.infrastructure.jpa.entity.JpaAccount;
import com.example.auth.infrastructure.jpa.mapper.JpaMapper;
import com.example.auth.infrastructure.jpa.repository.JpaAccountRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Adapter JPA -> Domain: AccountRepositoryImpl
 * Tidak memakai stereotype; di-wire lewat BeanConfig (Factory-only).
 */
public class AccountRepositoryImpl implements AccountRepository {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "username");

    private final JpaAccountRepository repo;

    public AccountRepositoryImpl(JpaAccountRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Account> findByUsername(String username) {
        return repo.findByUsername(username).map(JpaMapper::toDomain);
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return repo.findByEmail(email).map(JpaMapper::toDomain);
    }

    @Override
    public Optional<Account> findByUsernameOrEmail(String usernameOrEmail) {
        return repo.findByUsernameIgnoreCaseOrEmailIgnoreCase(usernameOrEmail, usernameOrEmail)
                .map(JpaMapper::toDomain);
    }

    @Override
    public Account save(Account a) {
        return JpaMapper.toDomain(repo.save(JpaMapper.toEntity(a)));
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return repo.findById(id).map(JpaMapper::toDomain);
    }

    @Override
    public PageResult<Account> search(String q, List<String> sort, int page, int size) {
        var spec = buildSpec(q);
        var pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), buildSort(sort));
        var result = repo.findAll(spec, pageable);
        var content = result.getContent().stream().map(JpaMapper::toDomain).toList();
        return new PageResult<>(content, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    private static Specification<JpaAccount> buildSpec(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) {
                return cb.conjunction();
            }
            String like = "%" + q.toLowerCase(Locale.ROOT) + "%";
            return cb.like(cb.lower(root.get("username")), like);
        };
    }

    private static Sort buildSort(List<String> sorts) {
        if (sorts == null || sorts.isEmpty()) {
            return Sort.by(Sort.Order.asc("username"));
        }
        var orders = new ArrayList<Sort.Order>();
        for (int i = 0; i < sorts.size(); i++) {
            String raw = sorts.get(i);
            if (raw == null || raw.isBlank()) {
                continue;
            }
            String field;
            String dirToken = null;
            if (raw.contains(",")) {
                String[] parts = raw.split(",", 2);
                field = parts[0].trim();
                if (parts.length > 1) {
                    dirToken = parts[1].trim();
                }
            } else {
                field = raw.trim();
                if (i + 1 < sorts.size()) {
                    String next = sorts.get(i + 1);
                    if (next != null) {
                        String candidate = next.trim().toLowerCase(Locale.ROOT);
                        if (candidate.equals("asc") || candidate.equals("desc")) {
                            dirToken = candidate;
                            i++; // consume direction token
                        }
                    }
                }
            }

            if (!ALLOWED_SORT_FIELDS.contains(field)) {
                throw new IllegalArgumentException("invalid sort field: " + field);
            }

            Sort.Direction direction = Sort.Direction.ASC;
            if (dirToken != null && !dirToken.isBlank()) {
                String normalized = dirToken.trim().toLowerCase(Locale.ROOT);
                if (normalized.equals("desc")) {
                    direction = Sort.Direction.DESC;
                } else if (!normalized.equals("asc")) {
                    throw new IllegalArgumentException("invalid sort direction: " + dirToken);
                }
            }

            orders.add(new Sort.Order(direction, field));
        }
        return orders.isEmpty() ? Sort.by(Sort.Order.asc("username")) : Sort.by(orders);
    }
}
