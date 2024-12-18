package com.goodsmall.email;

import com.goodsmall.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final RedisService redisService;
    @Value("${spring.mail.username}")
    private String sender;

    public void sendEmail(String email,String certifyCode){

        //0.이미 존재하는 이메일이면 재발송
        if(redisService.getData(email) != null){
            redisService.deleteData(email);
        }
        //1. 이메일 발송
        mailSender.send(createMessage(email, certifyCode));
        log.info("메일전송완료: 이메일{} 인증번호{}",email,certifyCode);
    }

//    이메일 발송 내역
    private SimpleMailMessage createMessage(String email, String certifyCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email); //메일 수신자
        message.setFrom(sender); // 메일 발신자
        message.setSubject("굿즈몰 회원가입을 위한 인증번호입니다."); //메일 제목
        message.setText("인증번호: "+certifyCode); //메일본문
        return message;
    }

}
