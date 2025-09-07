package com.example.catalog.web.category;

import com.example.catalog.application.category.CategoryCommands;
import com.example.catalog.application.category.CategoryQueries;
import com.example.catalog_service.web.api.CategoryApi;
import com.example.catalog_service.web.model.Category;
import com.example.catalog_service.web.model.CategoryCreateRequest;
import com.example.catalog_service.web.model.CategoryUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class CategoryController implements CategoryApi {
	private final CategoryQueries categoryQueries;
	private final CategoryCommands categoryCommands;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('catalog:category:write')")
    public ResponseEntity<Category> createCategory(@Valid CategoryCreateRequest categoryCreateRequest) {
        var c = categoryCommands.create(categoryCreateRequest.getName(), categoryCreateRequest.getParentId(), categoryCreateRequest.getActive(), categoryCreateRequest.getSortOrder());
        return ResponseEntity.status(201).body(map(c));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('catalog:category:write')")
    public ResponseEntity<Void> deleteCategory(UUID id) {
        categoryCommands.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<Category>> listCategories(Boolean active, UUID parentId) {
        return ResponseEntity.ok(categoryQueries.list(active, parentId).stream().map(CategoryController::map).toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('catalog:category:write')")
    public ResponseEntity<Category> updateCategory(UUID id, @Valid CategoryUpdateRequest categoryUpdateRequest) {
        var c = categoryCommands.update(id, categoryUpdateRequest.getName(), categoryUpdateRequest.getParentId(), categoryUpdateRequest.getActive(), categoryUpdateRequest.getSortOrder());
        return ResponseEntity.ok(map(c));
    }

    private static Category map(com.example.catalog.domain.category.Category c) {
        return new Category()
                .id(c.getId())
                .parentId(c.getParentId())
                .name(c.getName())
                .active(c.isActive())
                .sortOrder(c.getSortOrder());
    }
}
