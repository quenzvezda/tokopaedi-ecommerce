package com.example.iam.infrastructure.jpa;

import com.example.iam.domain.common.PageResult;
import com.example.iam.domain.permission.Permission;
import com.example.iam.domain.permission.PermissionRepository;
import com.example.iam.infrastructure.jpa.entity.PermissionJpa;
import com.example.iam.infrastructure.jpa.mapper.JpaMapper;
import com.example.iam.infrastructure.jpa.repository.JpaPermissionRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PermissionRepositoryImpl implements PermissionRepository {
    private final JpaPermissionRepository repo;

    @Override
    public Permission save(Permission p) {
        var saved = repo.save(JpaMapper.toEntity(p));
        return JpaMapper.toDomain(saved);
    }

    @Override
    public List<Permission> saveAll(Collection<Permission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return List.of();
        }
        var entities = permissions.stream()
                .map(JpaMapper::toEntity)
                .toList();
        return repo.saveAll(entities).stream()
                .map(JpaMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return repo.findById(id).map(JpaMapper::toDomain);
    }

    @Override
    public Optional<Permission> findByName(String name) {
        return repo.findByName(name).map(JpaMapper::toDomain);
    }

    @Override
    public List<Permission> findAll() {
        return repo.findAll().stream().map(JpaMapper::toDomain).toList();
    }

    @Override
    public PageResult<Permission> findAllPaged(int page, int size) {
        var p = repo.findAll(PageRequest.of(Math.max(0, page), Math.max(1, size)));
        var content = p.getContent().stream().map(JpaMapper::toDomain).toList();
        return new PageResult<>(content, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    @Override
    public PageResult<Permission> search(String q, List<String> sort, int page, int size) {
        var spec = buildPermissionSpec(q);
        var pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), buildSort(sort));
        var p = repo.findAll(spec, pageable);
        var content = p.getContent().stream().map(JpaMapper::toDomain).toList();
        return new PageResult<>(content, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<Permission> findAllByIds(Collection<Long> ids) {
        return repo.findAllById(ids).stream().map(JpaMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<String> findNamesByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return repo.findNamesByIds(ids);
    }

    private static Specification<PermissionJpa> buildPermissionSpec(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) {
                return cb.conjunction();
            }
            var tokens = Arrays.stream(q.trim().split("\\s+")).filter(s -> !s.isBlank()).toList();
            var andPredicates = new ArrayList<Predicate>();
            for (var t : tokens) {
                String like = "%" + t.toLowerCase() + "%";
                var nameLike = cb.like(cb.lower(root.get("name")), like);
                var descLike = cb.like(cb.lower(root.get("description")), like);
                andPredicates.add(cb.or(nameLike, descLike));
            }
            return andPredicates.isEmpty() ? cb.conjunction() : cb.and(andPredicates.toArray(new Predicate[0]));
        };
    }

    private static Sort buildSort(List<String> sorts) {
        if (sorts == null || sorts.isEmpty()) {
            // default sort by id desc to mimic createdAt:desc notion not present in schema
            return Sort.by(Sort.Order.desc("id"));
        }
        var allowed = java.util.Set.of("id", "name", "description");
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
                // Handle Spring splitting CSV into separate values: sort=name,ASC becomes ["name", "ASC"]
                if (i + 1 < sorts.size()) {
                    String next = sorts.get(i + 1);
                    if (next != null) {
                        String nd = next.trim().toLowerCase();
                        if (nd.equals("asc") || nd.equals("desc")) {
                            dirStr = next.trim();
                            i++; // consume next as direction
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
