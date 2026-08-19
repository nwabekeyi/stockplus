package com.stockmgmt.api.event;

import com.stockmgmt.api.service.impl.DashboardProjectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DashboardProjectionHandler {

    private final DashboardProjectionService projectionService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(DashboardStatsChangedEvent event) {
        projectionService.refreshReadModel(event.getStoreId());
    }
}
