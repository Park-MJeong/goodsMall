package com.goodsmall.user.service;

import com.goodsmall.email.EmailService;
import com.goodsmall.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final EmailService emailService;
    private final RedisService redisService;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private int timeout;


    public void certifyEmail(String email) {
//        1.인증코드 생성 후
        String certifyCode = createRandomCode();
        log.info("이메일 {} 인증코드생성 {}",email ,certifyCode);
//        2.인증메일을 발송
        emailService.sendEmail(email,certifyCode);
//        3.레디스에 해당정보 저장
        redisService.setData(email,certifyCode,timeout,TimeUnit.MILLISECONDS);
    }
//    인증코드 생성
    private String createRandomCode(){
        int startLimit = 48; // number 0~9 => 48~57
        int endLimit = 122; // alphabet 'A~Z' => 65~90 , 'a~z' =>97~122
        int targetStringLength = 7;
        Random random = new Random();

        return random.ints(startLimit, endLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 | i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }


}
