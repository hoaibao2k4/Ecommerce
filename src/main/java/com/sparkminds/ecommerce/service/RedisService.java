package com.sparkminds.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public void save(String key, String value, long timeout, TimeUnit unit) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        Objects.requireNonNull(unit);
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public String get(String key) {
        Objects.requireNonNull(key);
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        Objects.requireNonNull(key);
        redisTemplate.delete(key);
    }
}
