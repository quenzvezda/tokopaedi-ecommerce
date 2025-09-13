package com.example.inventory.web;

import com.example.inventory.application.stock.StockQueries;
import com.example.inventory.domain.stock.StockItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StockController.class)
@Import({GlobalExceptionHandler.class, WebTestConfig.class})
class StockControllerTest {

    @Autowired MockMvc mvc;
    @MockBean StockQueries stockQueries;

    @Test
    void bySku_returnsResponse() throws Exception {
        UUID sku = UUID.randomUUID();
        var item = StockItem.builder().skuId(sku).productId(UUID.randomUUID()).qtyOnHand(3).reserved(1).sellable(true).build();
        when(stockQueries.getBySkuId(sku)).thenReturn(item);

        mvc.perform(get("/inventory/api/v1/stock/"+sku).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skuId").value(sku.toString()))
                .andExpect(jsonPath("$.qtyOnHand").value(3))
                .andExpect(jsonPath("$.sellable").value(true));
    }

    @Test
    void bySku_notFound_returns404() throws Exception {
        UUID sku = UUID.randomUUID();
        when(stockQueries.getBySkuId(sku)).thenThrow(new NoSuchElementException());
        mvc.perform(get("/inventory/api/v1/stock/"+sku))
                .andExpect(status().isNotFound());
    }

    @Test
    void byProduct_returnsList() throws Exception {
        UUID pid = UUID.randomUUID();
        var list = List.of(
                StockItem.builder().skuId(UUID.randomUUID()).productId(pid).qtyOnHand(0).reserved(0).sellable(false).build(),
                StockItem.builder().skuId(UUID.randomUUID()).productId(pid).qtyOnHand(2).reserved(1).sellable(true).build()
        );
        when(stockQueries.getByProductId(pid)).thenReturn(list);

        mvc.perform(get("/inventory/api/v1/stock/product/"+pid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(pid.toString()))
                .andExpect(jsonPath("$[1].qtyOnHand").value(2));
    }

    @Test
    void bySku_invalidUuid_returns400() throws Exception {
        mvc.perform(get("/inventory/api/v1/stock/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void byProduct_invalidUuid_returns400() throws Exception {
        mvc.perform(get("/inventory/api/v1/stock/product/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }
}
