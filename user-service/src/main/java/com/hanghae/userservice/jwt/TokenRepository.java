package com.hanghae.userservice.jwt;

public interface TokenRepository {
    void saveRefreshToken(Long userId,String token, long expirationTime);
    String getRefreshToken(Long userId);
    Long getRefreshTokenTTL(Long userId);
    void deleteRefreshToken(Long userId);
    void addToBlacklist(String token, long expirationTime);
    boolean isBlacklisted(String token);
}
