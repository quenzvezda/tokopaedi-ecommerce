package com.example.catalog.web.product;

import com.example.catalog.application.product.ProductCommands;
import com.example.catalog.application.product.ProductQueries;
import com.example.catalog.domain.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost/dummy"
})
@AutoConfigureMockMvc
class ProductControllerSecurityTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ProductQueries productQueries;

    @MockBean
    ProductCommands productCommands;

    @Test
    void delete_allowsOwner() throws Exception {
        UUID id = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        when(productQueries.getById(id)).thenReturn(Product.builder().id(id).createdBy(owner).build());

        mvc.perform(delete("/catalog/api/v1/products/" + id)
                        .with(jwt().jwt(jwt -> jwt.subject(owner.toString()).claim("sub", owner.toString()))))
                .andExpect(status().isNoContent());

        verify(productCommands).delete(owner, id, false);
    }

    @Test
    void delete_forbiddenForNonOwnerWithoutAuthority() throws Exception {
        UUID id = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        UUID other = UUID.randomUUID();
        when(productQueries.getById(id)).thenReturn(Product.builder().id(id).createdBy(owner).build());

        mvc.perform(delete("/catalog/api/v1/products/" + id)
                        .with(jwt().jwt(jwt -> jwt.subject(other.toString()).claim("sub", other.toString()))))
                .andExpect(status().isForbidden());

        verify(productCommands, never()).delete(any(), eq(id), anyBoolean());
    }

}
