package com.example.catalog.application.product;

import com.example.catalog.domain.product.Product;
import com.example.catalog.domain.product.ProductRepository;
import com.example.catalog.domain.common.SlugGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class ProductCommandService implements ProductCommands {

    private final ProductRepository repo;

    @Override
    public Product create(UUID creatorId, String name, String shortDesc, UUID brandId, UUID categoryId, Boolean published) {
        String slug = generateSlug(name, null);
        Product p = Product.builder()
                .id(UUID.randomUUID())
                .name(name)
                .slug(slug)
                .shortDesc(shortDesc)
                .brandId(brandId)
                .categoryId(categoryId)
                .published(Boolean.TRUE.equals(published))
                .createdBy(creatorId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return repo.save(p);
    }

    @Override
    public Product update(UUID actorId, UUID id, String name, String shortDesc, UUID brandId, UUID categoryId, Boolean published, boolean overrideOwnership) {
        Product current = repo.findById(id).orElseThrow();
        if (!overrideOwnership) {
            if (actorId == null) {
                throw new AccessDeniedException("Actor is required to update product");
            }
            UUID createdBy = current.getCreatedBy();
            if (createdBy != null && !createdBy.equals(actorId)) {
                throw new AccessDeniedException("Only the product owner may update this product");
            }
        }
        if (name != null && !name.equals(current.getName())) {
            current.setName(name);
            current.setSlug(generateSlug(name, current.getId()));
        }
        current.setShortDesc(shortDesc != null ? shortDesc : current.getShortDesc());
        current.setBrandId(brandId != null ? brandId : current.getBrandId());
        current.setCategoryId(categoryId != null ? categoryId : current.getCategoryId());
        if (published != null) {
            current.setPublished(published);
        }
        current.setUpdatedAt(Instant.now());
        return repo.save(current);
    }

    @Override
    public void delete(UUID id) {
        repo.deleteById(id);
    }

    private String generateSlug(String name, UUID excludeId) {
        String base = SlugGenerator.slugify(name);
        String candidate = base;
        int idx = 1;
        while (repo.findBySlug(candidate)
                .filter(p -> excludeId == null || !p.getId().equals(excludeId))
                .isPresent()) {
            candidate = base + "-" + idx++;
        }
        return candidate;
    }
}
