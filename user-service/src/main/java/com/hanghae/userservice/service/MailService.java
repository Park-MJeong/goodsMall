package com.hanghae.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
@EnableAsync
public class MailService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String sender;

    @Async
    public void sendEmail(String email,String verifyCode){
        mailSender.send(createMessage(email, verifyCode));
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
