package com.jovanne.smartApi.infraestructure.data;

import com.jovanne.smartApi.domain.interfaces.ITokenService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TokenStore implements ITokenService {

    private final StringRedisTemplate redis;

    public TokenStore(StringRedisTemplate redis) {
        this.redis = redis;
    }

    private static final String TOKEN_PREFIX = "token:";
    private static final String REFRESH_PREFIX = "refreshToken:";

    private static final Duration TOKEN_TTL = Duration.ofMinutes(15);
    private static final Duration REFRESH_TTL = Duration.ofDays(8);


    public void saveToken(Long chatId, String token) {
        redis.opsForValue().set(TOKEN_PREFIX + chatId, token, TOKEN_TTL);
    }

    public void saveRefreshToken(Long chatId, String refreshToken) {
        redis.opsForValue().set(REFRESH_PREFIX + chatId, refreshToken, REFRESH_TTL);
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
