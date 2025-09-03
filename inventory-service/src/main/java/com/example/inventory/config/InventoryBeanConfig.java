package com.example.inventory.config;

import com.example.inventory.application.stock.*;
import com.example.inventory.domain.stock.ProcessedEventRepository;
import com.example.inventory.domain.stock.StockItemRepository;
import com.example.inventory.infrastructure.jpa.ProcessedEventRepositoryImpl;
import com.example.inventory.infrastructure.jpa.StockItemRepositoryImpl;
import com.example.inventory.infrastructure.jpa.repository.JpaProcessedEventRepository;
import com.example.inventory.infrastructure.jpa.repository.JpaStockItemRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryBeanConfig {

    // Adapters (Domain Repos -> JPA)
    @Bean
    StockItemRepository stockItemRepository(JpaStockItemRepository jpa) {
        return new StockItemRepositoryImpl(jpa);
    }

    @Bean
    ProcessedEventRepository processedEventRepository(JpaProcessedEventRepository jpa) {
        return new ProcessedEventRepositoryImpl(jpa);
    }

    // Use cases (Factory Pattern)
    @Bean
    StockCommands stockCommands(StockItemRepository stockRepo, ProcessedEventRepository eventRepo) {
        return new StockCommandService(stockRepo, eventRepo);
    }

    @Bean
    StockQueries stockQueries(StockItemRepository stockRepo) {
        return new StockQueryService(stockRepo);
    }
}

