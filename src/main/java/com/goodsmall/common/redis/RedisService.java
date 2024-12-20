package com.goodsmall.common.redis;

import com.goodsmall.common.exception.ErrorCode;
import com.goodsmall.common.exception.ErrorException;
import com.goodsmall.modules.user.dto.VerifyDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService {
    private final RedisTemplate<String,String> template;
    private HashOperations<String, String, String> hashOps;


    @PostConstruct
    public void init() {
        hashOps = template.opsForHash();  // template 초기화 후 hashOps 설정
    }

//    레디스에 인증정보저장
    public void setData(String email, String verifyCode,long timeout, TimeUnit timeUnit){
        // 인증코드 저장
        hashOps.put("goodsMall", email + ":verifyCode", verifyCode);
        template.expire("goodsMall", timeout, timeUnit);
    }

    public void checkData(VerifyDto dto){
        String email = dto.getEmail();
        String inputCode=dto.getEmail();
//        저장된 인증코드 조회
        String code = hashOps.get("goodsMall", email + ":verifyCode");

//        인증코드 검증
        if (code == null && !code.equals(inputCode)){
            throw new ErrorException(ErrorCode.VERIFICATION_CODE_INVALID);}

        hashOps.put("goodsMall", email + ":status", "true");
        template.expire("goodsMall", 10, TimeUnit.MINUTES); // 인증시 새로운 시간 부여
        log.info(hashOps.get("goodsMall", email + ":status"));
    }

    public String getData(String email){
        return hashOps.get("goodsMall", email);
    }

    public String getStatus(String email){
        return  hashOps.get("goodsMall", email + ":status");}

    public void deleteData(String email){
        template.delete(email);
    }

}
