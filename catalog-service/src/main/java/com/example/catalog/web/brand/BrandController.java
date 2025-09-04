package com.example.catalog.web.brand;

import com.example.catalog.application.brand.BrandCommands;
import com.example.catalog.application.brand.BrandQueries;
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
@Tag(name = "1. Brand")
public class BrandController {
	private final BrandQueries brandQueries;
	private final BrandCommands brandCommands;

    @GetMapping("/api/v1/brands")
	@Operation(operationId = "brand_1_list", summary = "List brands")
	public List<BrandResponse> brands(@RequestParam(required = false) Boolean active) {
		return brandQueries.list(active).stream().map(DtoMapper::toDto).toList();
	}

    @PostMapping("/api/v1/brands")
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:brand:write')")
	@Operation(operationId = "brand_2_create", summary = "Create brand", security = {@SecurityRequirement(name = "bearer-key")})
	public BrandResponse create(@RequestBody @Valid BrandCreateRequest req) {
		var b = brandCommands.create(req.name(), req.active());
		return DtoMapper.toDto(b);
	}

    @PutMapping("/api/v1/brands/{id}")
	@PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:brand:write')")
	@Operation(operationId = "brand_3_update", summary = "Update brand", security = {@SecurityRequirement(name = "bearer-key")})
	public BrandResponse update(@PathVariable UUID id, @RequestBody @Valid BrandUpdateRequest req) {
		var b = brandCommands.update(id, req.name(), req.active());
		return DtoMapper.toDto(b);
	}

    @DeleteMapping("/api/v1/brands/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAnyRole('ADMIN','CATALOG_EDITOR') or hasAuthority('SCOPE_product:brand:delete')")
	@Operation(operationId = "brand_4_delete", summary = "Delete brand", security = {@SecurityRequirement(name = "bearer-key")})
	public void delete(@PathVariable UUID id) {
		brandCommands.delete(id);
	}
}
