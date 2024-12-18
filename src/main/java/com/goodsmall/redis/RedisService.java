package com.goodsmall.redis;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

//    인증되었다면 유효기간 전에 값을 삭제해줌
    public boolean checkData(String email, String certifyCode){
        String data = hashOps.get("goodsMall", email);
        if(data != null &&data.equals((certifyCode))){
            template.delete(email);
            return true;
        }
        return false;
    }

    public String getData(String email){
        return hashOps.get("goodsMall", email);
    }

    public void deleteData(String email){
        template.delete(email);
    }

}
