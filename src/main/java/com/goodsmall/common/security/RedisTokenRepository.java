package com.goodsmall.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisTokenRepository implements TokenRepository {
    private final RedisTemplate<String,String> redisTemplate;
    private static final String REFRESH = "refresh:";

    @Override
    public void saveRefreshToken(Long userId, String token, long expirationTime) {
        redisTemplate.opsForValue().set(REFRESH+userId,token,expirationTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public String getRefreshToken(Long userId) {
        return redisTemplate.opsForValue().get(REFRESH+userId);
    }

    @Override
    public Long getRefreshTokenTTL(Long userId) {
        return redisTemplate.getExpire(REFRESH+userId,TimeUnit.MILLISECONDS);
    }

    @Override
    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete(REFRESH+userId);
    }

    @Override
    public void addToBlacklist(String token, long expirationTime) {
        String key = "blacklist:" + token;
        redisTemplate.opsForValue().set(key,"blacklisted",expirationTime, TimeUnit.MILLISECONDS);

    }

    @Override
    public boolean isBlacklisted(String token) {
        String key = "blacklist:" + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
