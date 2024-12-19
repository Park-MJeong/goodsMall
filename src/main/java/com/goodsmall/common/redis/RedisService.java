package com.goodsmall.common.redis;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

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
    public void setData(String email, String certifyCode,long timeout, TimeUnit timeUnit){
        hashOps.put("goodsMall", email, certifyCode);
        template.expire("goodsMall", timeout, timeUnit);
    }

//    인증코드 일치여부
    public boolean checkData(String email, String certifyCode){
        String data = hashOps.get("goodsMall", email);
        return data != null && data.equals((certifyCode));
    }

    public String getData(String email){
        return hashOps.get("goodsMall", email);
    }

    public void deleteData(String email){
        template.delete(email);
    }

}
