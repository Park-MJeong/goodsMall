package com.hanghae.userservice.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Slf4j(topic = "JwtTokenProvider")
@Component
public class JwtTokenProvider {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    private static SecretKey secretKey;

    public JwtTokenProvider(@Value("${spring.jwt.secret.key}") String jwtSecretKey) {
        secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * '토큰의 만료기간'을 지정하는 메서드
     *
     * @return {Date} Calendar
     */
    private static Date createExpiredDate() {
        Calendar c = Calendar.getInstance();
//        c.add(Calendar.SECOND, 3);        // 10초
//        c.add(Calendar.HOUR, 1);             // 1시간
        // c.add(Calendar.HOUR, 8);             // 8시간
         c.add(Calendar.DATE, 1);             // 1일
        return c.getTime();
    }

    /**
     * Access토큰생성
     *
     */
   public static String createAccessToken(Long userId, String email) {
        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("type","ACCESS");
        return BEARER_PREFIX+
                Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(createExpiredDate())
                        .signWith(secretKey, SignatureAlgorithm.HS256)
                        .compact();
    }

    /**
     * Refresh Token : 기간을 7일로 지정합니다.
     */
    private static Date createRefreshTokenExpiredDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7);
        return c.getTime();
    }

    public static String createRefreshToken(Long userId) {
        Claims claims = Jwts.claims();
        claims.put("userId :" ,userId);
        claims.put("type","REFRESH");
        log.debug("JWT Secret Key: " + secretKey);
        return BEARER_PREFIX+
                Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(createRefreshTokenExpiredDate())
                        .signWith(secretKey,SignatureAlgorithm.HS256)
                        .compact();
    }


    // JWT 토큰 substring
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        log.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }


    public boolean isValidToken(String token) {
        try {
            Claims claims = getTokenToClaims(token);
            log.info("발급완료:: expireTime :" + claims.getExpiration());
            log.info("email :" + claims.get("email"));
//            log.info("userName:" + claims.get("userName"));
            return true;
        } catch (ExpiredJwtException exception) {
            log.debug("token expired " + token);
            log.error("Token Expired" + exception);
            return false;
        } catch (JwtException exception) {
            log.debug("token expired " + token);
            log.error("Token Tampered" + exception);
            return false;
        } catch (NullPointerException exception) {
            log.debug("token expired " + token);
            log.error("Token is null" + exception);
            return false;
        }
    }

    /**
     * 'JWT' 내에서 'Claims' 정보를 반환하는 메서드
     *
     * @param token : 토큰
     * @return Claims : Claims
     */

    private static Claims getTokenToClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }
//    Cliam 내의 유저 아이디
    public static Long getClaimsToUserId(String token) {
        return  getTokenToClaims(token).get("userId", Long.class);
    }
    //    Cliam 내의 유저 이메일
    public static String getClaimsToUserEmail(String token) {
        return  getTokenToClaims(token).get("email", String.class);
    }
    //    Cliam 내의 토큰버전
    public static String getTokenVersion(String token) {
        return  getTokenToClaims(token).get("version", String.class);
    }

    public String getClaimsToTokenType(String token) {
        return  getTokenToClaims(token).get("type", String.class);
    }


    /**
     * Header 내에 토큰을 추출합니다.
     *
     * @param header 헤더
     * @return String
     */
    public static String getHeaderToToken(String header) {
        return header.split(" ")[1];
    }

//     토큰 유효시간
    public static long getExpirationTime(String refreshToken) {
        System.out.println(refreshToken);
        // Bearer 접두사 제거
        String token = refreshToken.replace(BEARER_PREFIX, "");
        System.out.println(token);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Date expiration = claims.getExpiration();
        Date now = new Date();

        // 만료시간 - 현재시간 = 남은시간 (밀리초 단위)
        return expiration.getTime() - now.getTime();
    }
//    public static long getIssuedTime(String refreshToken) {
//        System.out.println(refreshToken);
//        // Bearer 접두사 제거
//        String token = refreshToken.replace(BEARER_PREFIX, "");
//        System.out.println(token);
//
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(secretKey)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//
//        Date expiration = claims.getIssuedAt();
//        Date now = new Date();
//
//        // 만료시간 - 현재시간 = 남은시간 (밀리초 단위)
//        return expiration.getTime() - now.getTime();
//    }


}

