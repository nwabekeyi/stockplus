package com.stockmgmt.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.util.concurrent.Executor;

@Configuration
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        VirtualThreadTaskExecutor executor = new VirtualThreadTaskExecutor("dashboard-async-");
        log.info("Configured virtual-thread TaskExecutor for @Async");
        return executor;
    }
}
