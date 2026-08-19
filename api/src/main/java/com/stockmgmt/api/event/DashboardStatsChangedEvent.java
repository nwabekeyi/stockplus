package com.stockmgmt.api.event;

import java.util.UUID;

public class DashboardStatsChangedEvent {

    private final UUID storeId;
    private final long timestamp;

    public DashboardStatsChangedEvent(UUID storeId) {
        this.storeId = storeId;
        this.timestamp = System.currentTimeMillis();
    }

    public UUID getStoreId() {
        return storeId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
