package com.example.inventory.config;

import com.example.inventory.application.stock.StockCommands;
import com.example.inventory.application.stock.StockQueries;
import com.example.inventory.domain.stock.ProcessedEventRepository;
import com.example.inventory.domain.stock.StockItemRepository;
import com.example.inventory.infrastructure.jpa.repository.JpaProcessedEventRepository;
import com.example.inventory.infrastructure.jpa.repository.JpaStockItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@ContextConfiguration(classes = {InventoryBeanConfig.class})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class BeanConfigWiringTest {

    @MockBean JpaStockItemRepository jpaStockItemRepository;
    @MockBean JpaProcessedEventRepository jpaProcessedEventRepository;

    private final StockItemRepository stockItemRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final StockCommands stockCommands;
    private final StockQueries stockQueries;

    BeanConfigWiringTest(StockItemRepository stockItemRepository,
                         ProcessedEventRepository processedEventRepository,
                         StockCommands stockCommands,
                         StockQueries stockQueries) {
        this.stockItemRepository = stockItemRepository;
        this.processedEventRepository = processedEventRepository;
        this.stockCommands = stockCommands;
        this.stockQueries = stockQueries;
    }

    @Test
    void beans_present() {
        assertThat(stockItemRepository).isNotNull();
        assertThat(processedEventRepository).isNotNull();
        assertThat(stockCommands).isNotNull();
        assertThat(stockQueries).isNotNull();
    }
}

