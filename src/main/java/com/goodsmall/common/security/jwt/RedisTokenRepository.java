package com.goodsmall.common.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisTokenRepository implements TokenRepository {
    private final RedisTemplate<String,String> redisTemplate;
    private static final String REFRESH = "refresh:";

    @Override
    public void saveRefreshToken(Long userId,String token, long expirationTime) {
        String key = REFRESH + userId + ":" ;
        redisTemplate.opsForValue().set(key,token,expirationTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public String getRefreshToken(Long userId) {
        String key = REFRESH + userId + ":" ;

        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Long getRefreshTokenTTL(Long userId) {
        String key = REFRESH + userId + ":" ;

        return redisTemplate.getExpire(key,TimeUnit.MILLISECONDS);
    }

    @Override
    public void deleteRefreshToken(Long userId) {
        String key = REFRESH + userId + ":";

        redisTemplate.delete(key);
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

    public void deleteKeyByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys); // 검색된 키 삭제
        }
    }
}
