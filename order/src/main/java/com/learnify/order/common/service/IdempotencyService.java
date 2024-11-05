package com.learnify.order.common.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Slf4j
public class IdempotencyService {
    private final StringRedisTemplate redisTemplate;

    public boolean create(String idempotencyKey, long ttl) {
        Boolean success = redisTemplate
                .opsForValue()
                .setIfAbsent(idempotencyKey, "1", ttl, TimeUnit.SECONDS);

        return Boolean.TRUE.equals(success);
    }

    public boolean remove(String idempotencyKey) {
        Boolean success = redisTemplate.delete(idempotencyKey);
        return Boolean.TRUE.equals(success);
    }
}
