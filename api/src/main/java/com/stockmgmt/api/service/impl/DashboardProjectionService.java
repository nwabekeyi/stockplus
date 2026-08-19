package com.stockmgmt.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockmgmt.api.entity.DashboardStats;
import com.stockmgmt.api.entity.dto.response.DashboardStatsResponse;
import com.stockmgmt.api.repository.DashboardStatsRepository;
import com.stockmgmt.api.service.SaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardProjectionService {

    static final String CACHE_KEY_PREFIX = "dashboard:stats:";
    static final long CACHE_TTL_SECONDS = 60L;
    static final long REFRESH_THROTTLE_MS = 30_000L;

    private final SaleService saleService;
    private final DashboardStatsRepository dashboardStatsRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ConcurrentMap<UUID, ReentrantLock> locks = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, Long> lastRefresh = new ConcurrentHashMap<>();

    String cacheKey(UUID storeId) {
        return CACHE_KEY_PREFIX + storeId;
    }

    @Async
    public void refreshReadModel(UUID storeId) {
        long now = System.currentTimeMillis();
        if (shouldSkip(storeId, now)) {
            log.debug("Skipping projection refresh for store {} (throttled)", storeId);
            return;
        }

        ReentrantLock lock = locks.computeIfAbsent(storeId, k -> new ReentrantLock());
        lock.lock();
        try {
            if (shouldSkip(storeId, System.currentTimeMillis())) {
                return;
            }
            lastRefresh.put(storeId, System.currentTimeMillis());

            // Read side source of truth: recompute stats from Postgres (WRITE data store).
            DashboardStatsResponse stats = saleService.getDashboardStats(storeId);
            DashboardStats statsEntity = toEntity(storeId, stats);

            // Postgres read-model write (guarded by ReentrantLock, blocking acquire).
            dashboardStatsRepository.save(statsEntity);
            log.debug("Refreshed dashboard projection (Postgres) for store {}", storeId);

            try {
                redisTemplate.opsForValue().set(
                        cacheKey(storeId),
                        toJson(stats),
                        CACHE_TTL_SECONDS,
                        TimeUnit.SECONDS);
            } catch (DataAccessException dae) {
                log.warn("Failed to update Redis cache for store {}: {}", storeId, dae.getMessage());
            }
        } catch (Exception e) {
            log.warn("Failed to refresh dashboard projection for store {}", storeId, e);
        } finally {
            lock.unlock();
        }
    }

    private boolean shouldSkip(UUID storeId, long now) {
        Long last = lastRefresh.get(storeId);
        return last != null && (now - last) < REFRESH_THROTTLE_MS;
    }

    private DashboardStats toEntity(UUID storeId, DashboardStatsResponse stats) {
        return DashboardStats.builder()
                .storeId(storeId)
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
                .lastUpdated(java.time.Instant.now())
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
