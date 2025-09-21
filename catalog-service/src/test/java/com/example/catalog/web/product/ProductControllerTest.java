package com.example.catalog.web.product;

import com.example.catalog.application.product.ProductCommands;
import com.example.catalog.application.product.ProductQueries;
import com.example.catalog.domain.common.PageResult;
import com.example.catalog.domain.product.Product;
import com.example.catalog.web.GlobalExceptionHandler;
import com.example.catalog.web.dto.ProductCreateRequest;
import com.example.catalog.web.dto.ProductUpdateRequest;
import com.example.common.web.response.ErrorProps;
import com.example.common.web.response.ErrorResponseBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class ProductControllerTest {

    @Mock ProductQueries productQueries;
    @Mock ProductCommands productCommands;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        ErrorProps props = new ErrorProps();
        props.setVerbose(false);
        ErrorResponseBuilder errorBuilder = new ErrorResponseBuilder(props, "catalog-service");
        GlobalExceptionHandler handler = new GlobalExceptionHandler(errorBuilder);
        ProductController controller = new ProductController(productQueries, productCommands);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(handler)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void list_ok() throws Exception {
        var p = Product.builder().id(UUID.randomUUID()).name("Prod").slug("prod").shortDesc("d")
                .brandId(UUID.randomUUID()).categoryId(UUID.randomUUID())
                .createdBy(UUID.randomUUID())
                .published(true).createdAt(Instant.now()).updatedAt(Instant.now()).build();
        when(productQueries.search("shoe", null, null, 1, 2)).thenReturn(PageResult.of(List.of(p), 1, 2, 10));

        mvc.perform(get("/catalog/api/v1/products").param("q", "shoe").param("page", "1").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Prod"))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(10));
    }

    @Test
    void detail_ok() throws Exception {
        UUID creatorId = UUID.randomUUID();
        var p = Product.builder().id(UUID.randomUUID()).name("Prod").slug("prod").shortDesc("d")
                .brandId(UUID.randomUUID()).categoryId(UUID.randomUUID())
                .createdBy(creatorId)
                .published(true).createdAt(Instant.now()).updatedAt(Instant.now()).build();
        when(productQueries.getBySlug("prod")).thenReturn(p);
        mvc.perform(get("/catalog/api/v1/products/prod"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("prod"))
                .andExpect(jsonPath("$.createdBy").value(creatorId.toString()));
    }

    @Test
    void create_ok() throws Exception {
        UUID brandId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID creatorId = UUID.randomUUID();
        var p = Product.builder().id(UUID.randomUUID()).name("P").slug("p").shortDesc("d")
                .brandId(brandId).categoryId(categoryId)
                .createdBy(creatorId)
                .published(true).createdAt(Instant.now()).updatedAt(Instant.now()).build();
        lenient().when(productCommands.create(eq(creatorId), any(), any(), any(), any(), any())).thenReturn(p);

        setAuthentication(creatorId, List.of());
        var om = new com.fasterxml.jackson.databind.ObjectMapper();
        mvc.perform(post("/catalog/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(new ProductCreateRequest("P", "d", brandId, categoryId, true))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("p"))
                .andExpect(jsonPath("$.createdBy").value(creatorId.toString()));
    }

    @Test
    void update_ok_asOwner() throws Exception {
        UUID id = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        var product = Product.builder().id(id).name("PP").slug("pp").shortDesc("d")
                .brandId(UUID.randomUUID()).categoryId(UUID.randomUUID())
                .createdBy(owner)
                .published(true).createdAt(Instant.now()).updatedAt(Instant.now()).build();
        when(productQueries.getById(id)).thenReturn(product);
        lenient().when(productCommands.update(eq(owner), eq(id), any(), any(), any(), any(), any(), eq(false))).thenReturn(product);

        setAuthentication(owner, List.of());
        var om = new com.fasterxml.jackson.databind.ObjectMapper();
        mvc.perform(put("/catalog/api/v1/products/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(new ProductUpdateRequest("PP", "d", null, null, true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("pp"));
    }

    @Test
    void update_forbidden_whenNotOwnerAndNoOverride() throws Exception {
        UUID id = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        var product = Product.builder().id(id).createdBy(owner).build();
        when(productQueries.getById(id)).thenReturn(product);
        lenient().when(productCommands.update(any(), eq(id), any(), any(), any(), any(), any(), eq(false)))
                .thenThrow(new AccessDeniedException("forbidden"));

        setAuthentication(UUID.randomUUID(), List.of());
        mvc.perform(put("/catalog/api/v1/products/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper()
                        .writeValueAsBytes(new ProductUpdateRequest("Name", null, null, null, true))))
                .andExpect(status().isForbidden());
    }

    @Test
    void update_ok_withOverrideAuthority() throws Exception {
        UUID id = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        var product = Product.builder().id(id).name("PP").slug("pp")
                .createdBy(owner)
                .build();
        when(productQueries.getById(id)).thenReturn(product);
        lenient().when(productCommands.update(any(), eq(id), any(), any(), any(), any(), any(), eq(true))).thenReturn(product);

        setAuthentication(UUID.randomUUID(), List.of("catalog:product:update"));
        mvc.perform(put("/catalog/api/v1/products/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new com.fasterxml.jackson.databind.ObjectMapper()
                        .writeValueAsBytes(new ProductUpdateRequest("Name", null, null, null, true))))
                .andExpect(status().isOk());
    }

    @Test
    void delete_ok_withOverrideAuthority() throws Exception {
        UUID id = UUID.randomUUID();
        UUID actor = UUID.randomUUID();
        setAuthentication(actor, List.of("catalog:product:delete"));
        mvc.perform(delete("/catalog/api/v1/products/" + id))
                .andExpect(status().isNoContent());
        verify(productCommands).delete(actor, id, true);
    }

    @Test
    void delete_ok_asOwner() throws Exception {
        UUID id = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        setAuthentication(owner, List.of());
        mvc.perform(delete("/catalog/api/v1/products/" + id))
                .andExpect(status().isNoContent());
        verify(productCommands).delete(owner, id, false);
    }

    private static void setAuthentication(UUID subject, List<String> authorities) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(subject.toString())
                .claim("sub", subject.toString())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        List<SimpleGrantedAuthority> granted = authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        Authentication auth = new JwtAuthenticationToken(jwt, granted);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}



