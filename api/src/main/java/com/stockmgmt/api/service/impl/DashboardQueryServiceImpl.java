package com.stockmgmt.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockmgmt.api.entity.DashboardStats;
import com.stockmgmt.api.entity.dto.response.DashboardStatsResponse;
import com.stockmgmt.api.repository.DashboardStatsRepository;
import com.stockmgmt.api.service.DashboardQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardQueryServiceImpl implements DashboardQueryService {

    private final DashboardStatsRepository dashboardStatsRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DashboardProjectionService projectionService;

    @Override
    public DashboardStatsResponse getDashboardStats(UUID storeId) {
        String key = DashboardProjectionService.CACHE_KEY_PREFIX + storeId;
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            // Cache hit: circuit breaker served from Redis.
            // Background worker refreshes the readable (Postgres) data and the cache as well.
            projectionService.refreshReadModel(storeId);
            try {
                return objectMapper.readValue(cached, DashboardStatsResponse.class);
            } catch (JsonProcessingException e) {
                log.warn("Failed to deserialize cached dashboard stats for store {}", storeId);
            }
        }

        // Cache miss: do only Postgres (circuit breaker protects the DB below).
        DashboardStatsResponse stats = dashboardStatsRepository.findByStoreId(storeId)
                .map(this::toResponse)
                .orElseGet(this::defaultStats);

        try {
            redisTemplate.opsForValue().set(
                    key,
                    toJson(stats),
                    DashboardProjectionService.CACHE_TTL_SECONDS,
                    TimeUnit.SECONDS);
        } catch (DataAccessException dae) {
            log.warn("Failed to populate Redis cache for store {}: {}", storeId, dae.getMessage());
        }

        return stats;
    }

    private DashboardStatsResponse toResponse(DashboardStats stats) {
        return DashboardStatsResponse.builder()
                .totalProducts(stats.getTotalProducts())
                .lowStockCount(stats.getLowStockCount())
                .totalSalesToday(stats.getTotalSalesToday())
                .revenueToday(stats.getRevenueToday())
                .revenueThisMonth(stats.getRevenueThisMonth())
                .totalSalesThisMonth(stats.getTotalSalesThisMonth())
                .customerDebt(stats.getCustomerDebt())
                .supplierDebt(stats.getSupplierDebt())
                .expensesToday(stats.getExpensesToday())
                .totalCustomers(stats.getTotalCustomers())
                .totalSuppliers(stats.getTotalSuppliers())
                .build();
    }

    private DashboardStatsResponse defaultStats() {
        return DashboardStatsResponse.builder()
                .totalProducts(0)
                .lowStockCount(0)
                .totalSalesToday(0)
                .revenueToday(java.math.BigDecimal.ZERO)
                .revenueThisMonth(java.math.BigDecimal.ZERO)
                .totalSalesThisMonth(0)
                .customerDebt(java.math.BigDecimal.ZERO)
                .supplierDebt(java.math.BigDecimal.ZERO)
                .expensesToday(java.math.BigDecimal.ZERO)
                .totalCustomers(0)
                .totalSuppliers(0)
                .build();
    }

    private String toJson(DashboardStatsResponse stats) {
        try {
            return objectMapper.writeValueAsString(stats);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize dashboard stats", e);
        }
    }
}
