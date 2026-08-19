package com.stockmgmt.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConnectionValidator implements ApplicationRunner {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void run(ApplicationArguments args) {
        validateRedis();
    }

    private void validateRedis() {
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to connect to Redis: " + e.getMessage(), e);
        }
    }
}
