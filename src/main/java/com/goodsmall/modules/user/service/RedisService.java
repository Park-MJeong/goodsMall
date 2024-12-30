package com.goodsmall.modules.user.service;


import com.goodsmall.common.constant.ErrorCode;
import com.goodsmall.common.exception.BusinessException;
import com.goodsmall.modules.user.dto.VerifyDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService {
    private final RedisTemplate<String,String> redisTemplate;
    private HashOperations<String, String, String> hashOps;
    private static final String GOODS_MALL = "goodsMall";
    private static final String REFRESH = "refresh:";


    @PostConstruct
    public void init() {
        hashOps = redisTemplate.opsForHash();  // template 초기화 후 hashOps 설정
    }


    /**
     *  인증코드 저장
     */
    public void setData(String email, String verifyCode){
        hashOps.put(GOODS_MALL, email + ":verifyCode", verifyCode);
        hashOps.put(GOODS_MALL, email + ":status", "false");
        redisTemplate.expire(GOODS_MALL, Duration.ofMinutes(5));
        log.info("레디스에 인증코드저장{}",hashOps.get("goodsMall", email + ":verifyCode"));
    }

    /**
     *  저장된 코드와 일치 확인
     */
    public void checkData(VerifyDto dto){
        String email = dto.getEmail();
        String inputCode=dto.getVerifyCode();
//        저장된 인증코드 조회
        String code = hashOps.get(GOODS_MALL, email + ":verifyCode");
        log.info("레디스에 저장된 인증코드 {}", hashOps.get(GOODS_MALL, email + ":verifyCode"));

        if (code == null || !code.equals(inputCode)){
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_INVALID);}
        else{
            hashOps.put(GOODS_MALL, email + ":status", "true");
            redisTemplate.expire(GOODS_MALL,  Duration.ofMinutes(30)); // 인증시 새로운 시간 부여
            log.info(hashOps.get(GOODS_MALL, email + ":status"));
        }

    }
    /**
     *  해당 이메일로 저장된 코드 정보가 있는지 확인
     */
    public boolean isEmailExist(String email){
        String value = hashOps.get(GOODS_MALL, email + ":verifyCode");
        return value!=null;
    }
//    /**
//     * 블랙리스트에 토큰 저장
//     */
//    public void saveBlacklistToken(String token, Long expirationTime) {
//        String key = "blacklist:" + token;
//        redisTemplate.opsForValue().set(key, "blacklisted", expirationTime, TimeUnit.MILLISECONDS);
//    }
//
//    /**
//     * 블랙리스트 토큰 확인
//     */
//    public boolean isTokenBlacklisted(String token) {
//        String key = "blacklist:" + token;
//        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
//    }
//
//    /**
//     *  refresh토큰 저장
//     */
//    public void saveRefreshToken(Long id,String refreshToken,Long expirationTime){
//        redisTemplate.opsForValue().set(REFRESH+id,refreshToken,expirationTime, TimeUnit.MILLISECONDS);
//
//    }
//
//    // Refresh Token TTL 조회
//    public Long getRefreshTokenTTL(Long id) {
//        return redisTemplate.getExpire(REFRESH+id, TimeUnit.MILLISECONDS);
//    }
//    // Refresh Token 조회
//    public String getRefreshToken(Long id) {
//        return redisTemplate.opsForValue().get(REFRESH+id);
//    }
//
//    // Refresh Token 삭제
//    public void deleteRefreshToken(Long id) {
//        redisTemplate.delete(REFRESH+id);
//    }


    public String getStatus(String email){
        return hashOps.get(GOODS_MALL, email + ":status");
    }


}
