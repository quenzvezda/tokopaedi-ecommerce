package com.example.iam.infrastructure.jpa;

import com.example.iam.domain.common.PageResult;
import com.example.iam.domain.role.Role;
import com.example.iam.domain.role.RoleRepository;
import com.example.iam.infrastructure.jpa.mapper.JpaMapper;
import com.example.iam.infrastructure.jpa.repository.JpaRoleRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {
    private final JpaRoleRepository repo;

    @Override
    public Role save(Role r) {
        var saved = repo.save(JpaMapper.toEntity(r));
        return JpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Role> findById(Long id) {
        return repo.findById(id).map(JpaMapper::toDomain);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return repo.findByName(name).map(JpaMapper::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return repo.findAll().stream().map(JpaMapper::toDomain).toList();
    }

    @Override
    public PageResult<Role> findAllPaged(int page, int size) {
        var p = repo.findAll(PageRequest.of(Math.max(0, page), Math.max(1, size)));
        var content = p.getContent().stream().map(JpaMapper::toDomain).toList();
        return new PageResult<>(content, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    @Override
    public PageResult<Role> search(String q, List<String> sort, int page, int size) {
        var spec = buildRoleSpec(q);
        var pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), buildSort(sort));
        var p = repo.findAll(spec, pageable);
        var content = p.getContent().stream().map(JpaMapper::toDomain).toList();
        return new PageResult<>(content, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    private static Specification<com.example.iam.infrastructure.jpa.entity.RoleJpa> buildRoleSpec(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return cb.conjunction();
            var tokens = Arrays.stream(q.trim().split("\\s+")).filter(s -> !s.isBlank()).toList();
            var andPreds = new java.util.ArrayList<Predicate>();
            for (var t : tokens) {
                String like = "%" + t.toLowerCase() + "%";
                andPreds.add(cb.like(cb.lower(root.get("name")), like));
            }
            return andPreds.isEmpty() ? cb.conjunction() : cb.and(andPreds.toArray(new Predicate[0]));
        };
    }

    private static Sort buildSort(List<String> sorts) {
        if (sorts == null || sorts.isEmpty()) {
            return Sort.by(Sort.Order.desc("id"));
        }
        var allowed = java.util.Set.of("id", "name");
        var orders = new ArrayList<Sort.Order>();
        for (int i = 0; i < sorts.size(); i++) {
            String raw = sorts.get(i);
            if (raw == null || raw.isBlank()) continue;

            String field;
            String dirStr = null;
            if (raw.contains(",")) {
                String[] parts = raw.split(",");
                field = parts[0].trim();
                if (parts.length > 1) dirStr = parts[1].trim();
            } else {
                field = raw.trim();
                if (i + 1 < sorts.size()) {
                    String next = sorts.get(i + 1);
                    if (next != null) {
                        String nd = next.trim().toLowerCase();
                        if (nd.equals("asc") || nd.equals("desc")) {
                            dirStr = next.trim();
                            i++; // consume next token as direction
                        }
                    }
                }
            }

            if (!allowed.contains(field)) {
                throw new IllegalArgumentException("invalid sort field: " + field);
            }
            Sort.Direction dir = Sort.Direction.ASC;
            if (dirStr != null && !dirStr.isBlank()) {
                var d = dirStr.trim().toLowerCase();
                if (d.equals("desc")) dir = Sort.Direction.DESC;
                else if (!d.equals("asc")) throw new IllegalArgumentException("invalid sort direction: " + dirStr);
            }
            orders.add(new Sort.Order(dir, field));
        }
        return orders.isEmpty() ? Sort.by(Sort.Order.asc("id")) : Sort.by(orders);
    }
}
