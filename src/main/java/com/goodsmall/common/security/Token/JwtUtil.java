package com.goodsmall.common.security.Token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    private static SecretKey JWT_SECRET_KEY;

    public JwtUtil(@Value("${spring.jwt.secret.key}") String jwtSecretKey) {
        JwtUtil.JWT_SECRET_KEY = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * '토큰의 만료기간'을 지정하는 메서드
     *
     * @return {Date} Calendar
     */
    private static Date createExpiredDate() {
        Calendar c = Calendar.getInstance();
//        c.add(Calendar.SECOND, 3);        // 10초
        c.add(Calendar.HOUR, 1);             // 1시간
        // c.add(Calendar.HOUR, 8);             // 8시간
        // c.add(Calendar.DATE, 1);             // 1일
        return c.getTime();
    }

    /**
     * Access토큰생성
     *
     */
   public String createAccessToken(Long userId,String userName) {
        Claims claims = Jwts.claims();
        claims.put("userId :" , userId);
        claims.put("userName", userName);
        claims.put("type","ACCESS");
        return BEARER_PREFIX+
                Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(createExpiredDate())
                        .signWith(JWT_SECRET_KEY,SignatureAlgorithm.HS256)
                        .compact();
    }

    /**
     * Refresh Token : 기간을 14일로 지정합니다.
     */
    private static Date createRefreshTokenExpiredDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7);
        return c.getTime();
    }

    public static String generateRefreshToken(Long userId,String userName) {
        Claims claims = Jwts.claims();
        claims.put("userId :" ,userId);
        claims.put("userName", userName);
        claims.put("type","REFRESH");
        log.debug("JWT Secret Key: " + JWT_SECRET_KEY);
        return BEARER_PREFIX+
                Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(createRefreshTokenExpiredDate())
                        .signWith(JWT_SECRET_KEY,SignatureAlgorithm.HS256)
                        .compact();
    }


    /**
     * 토큰을 기반으로 유효한 토큰인지 여부를 반환해주는 메서드
     * - Claim 내에서 사용자 정보를 추출합니다.
     *
     * @param token String  : 토큰
     * @return boolean      : 유효한지 여부 반환
     */
    public static boolean isValidToken(String token) {
        try {
            Claims claims = getTokenToClaims(token);
            log.info("expireTime :" + claims.getExpiration());
            log.info("userEmail :" + claims.get("userEmail"));
            log.info("userName:" + claims.get("userName"));
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
        return Jwts.parserBuilder().setSigningKey(JWT_SECRET_KEY).build().parseClaimsJws(token).getBody();
    }
//    Cliam 내의 유저 아이디
    public String getClaimsToUserId(String token) {
        return  getTokenToClaims(token).get("userId", String.class);
    }
    //    Cliam 내의 유저 이름
    public String getClaimsToUserName(String token) {
        return  getTokenToClaims(token).get("userName", String.class);
    }

    /**
     * Header 내에 토큰을 추출합니다.
     *
     * @param header 헤더
     * @return String
     */
    public static String getTokenFromHeader(String header) {
        return header.split(" ")[1];
    }

}

