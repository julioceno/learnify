package com.learnify.order.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnify.order.common.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

// TODO: adicionar logs
@Service
@AllArgsConstructor
@Slf4j
public class IdempotencyService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public boolean create(String idempotencyKey, DataDTO data, long ttl) {
        try {
            String dataSerialized = objectMapper.writeValueAsString(data);
            Boolean success = redisTemplate
                    .opsForValue()
                    .setIfAbsent(idempotencyKey, dataSerialized, ttl, TimeUnit.SECONDS);

            return Boolean.TRUE.equals(success);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Não foi possível prosseguir com a assinatura");
        }
    }

    public boolean update(String idempotencyKey, DataDTO newData, long ttl) {
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(idempotencyKey))) {
                String dataSerialized = objectMapper.writeValueAsString(newData);
                redisTemplate.opsForValue().set(idempotencyKey, dataSerialized, ttl, TimeUnit.SECONDS);
                return true;
            }

            log.warn("Key not found: {}", idempotencyKey);
            return false;
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Não foi possível prosseguir com a assinatura");
        }
    }

    public DataDTO get(String idempotencyKey) {
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(idempotencyKey))) {
                String data = redisTemplate.opsForValue().get(idempotencyKey);
                DataDTO dataDTO = objectMapper.readValue(data, DataDTO.class);
                return dataDTO;
            }

            log.warn("Key not found: {}", idempotencyKey);
            return null;
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Não foi obter o valor");
        }
    }

    public void remove(String idempotencyKey) {
        redisTemplate.delete(idempotencyKey);
    }
}
