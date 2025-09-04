package com.example.catalog.web.category;

import com.example.catalog.application.category.CategoryCommands;
import com.example.catalog.application.category.CategoryQueries;
import com.example.catalog.web.dto.*;
import com.example.catalog.web.mapper.DtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "2. Category")
public class CategoryController {
	private final CategoryQueries categoryQueries;
	private final CategoryCommands categoryCommands;

    @GetMapping("/api/v1/categories")
	@Operation(operationId = "category_1_list", summary = "List categories")
	public List<CategoryResponse> categories(@RequestParam(required = false) Boolean active,
	                                         @RequestParam(required = false) UUID parentId) {
		return categoryQueries.list(active, parentId).stream().map(DtoMapper::toDto).toList();
	}

    @PostMapping("/api/v1/categories")
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:category:write')")
	@Operation(operationId = "category_2_create", summary = "Create category", security = {@SecurityRequirement(name = "bearer-key")})
	public CategoryResponse create(@RequestBody @Valid CategoryCreateRequest req) {
		var c = categoryCommands.create(req.name(), req.parentId(), req.active(), req.sortOrder());
		return DtoMapper.toDto(c);
	}

    @PutMapping("/api/v1/categories/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:category:write')")
	@Operation(operationId = "category_3_update", summary = "Update category", security = {@SecurityRequirement(name = "bearer-key")})
	public CategoryResponse update(@PathVariable UUID id, @RequestBody @Valid CategoryUpdateRequest req) {
		var c = categoryCommands.update(id, req.name(), req.parentId(), req.active(), req.sortOrder());
		return DtoMapper.toDto(c);
	}

    @DeleteMapping("/api/v1/categories/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:category:delete')")
	@Operation(operationId = "category_4_delete", summary = "Delete category", security = {@SecurityRequirement(name = "bearer-key")})
	public void delete(@PathVariable UUID id) {
		categoryCommands.delete(id);
	}
}
