package com.jovanne.smartApi.infraestructure.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class TokenStore {
    @Autowired
    RedisTemplate<String, String> redis;

    private static final String TOKEN_PREFIX = "token:";
    private static final String REFRESH_PREFIX = "refreshToken:";


    public void saveToken(Long chatId, String token, Duration ttl) {
        redis.opsForValue().set(TOKEN_PREFIX + chatId, token, ttl);
    }

    public void saveRefreshToken(Long chatId, String refreshToken, Duration ttl) {
        redis.opsForValue().set(REFRESH_PREFIX + chatId, refreshToken, ttl);
    }

    public String getToken(Long chatId) {
        return redis.opsForValue().get(TOKEN_PREFIX + chatId);
    }

    public String getRefreshTokenByChatId(Long chatId) {
        return redis.opsForValue().get(REFRESH_PREFIX + chatId);
    }

    public boolean hasToken(Long chatId) {
        return Boolean.TRUE.equals(redis.hasKey(TOKEN_PREFIX + chatId));
    }

    public void deleteAll(Long chatId) {
        redis.delete(TOKEN_PREFIX + chatId);
        redis.delete(REFRESH_PREFIX + chatId);
    }
}
