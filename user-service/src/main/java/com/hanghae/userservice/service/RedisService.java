package com.hanghae.userservice.service;

import com.hanghae.common.exception.ErrorCode;
import com.hanghae.common.exception.BusinessException;
import com.hanghae.userservice.dto.VerifyDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService {
    private final RedisTemplate<String,String> redisTemplate;
    private HashOperations<String, String, String> hashOps;
    private static final String GOODS_MALL = "goodsMall";

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

    public String getStatus(String email){
        return hashOps.get(GOODS_MALL, email + ":status");
    }


}
