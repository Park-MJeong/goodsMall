package com.goodsmall.modules.user.service;

import com.goodsmall.common.email.EmailService;
import com.goodsmall.common.redis.RedisService;
import com.goodsmall.modules.user.dto.UserRequestDto;
import com.goodsmall.modules.user.domain.User;
import com.goodsmall.modules.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final EmailService emailService;
    private final RedisService redisService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private int timeout;


    public UserRequestDto signup(UserRequestDto requestDto) {
        log.info("회원가입: 유저이메일{} 인증코드{}",requestDto.getEmail(),requestDto.getCertifyCode());

        boolean checkData = redisService.checkData(requestDto.getEmail(), requestDto.getCertifyCode());
        if (checkData) {
            String password=passwordEncoder.encode(requestDto.getPassword()); //비밀번호암호화
            User user =new User(
                    requestDto.getUserName(),
                    requestDto.getPhoneNumber(),
                    requestDto.getAddress(),
                    requestDto.getEmail(),
                    password
            );
            userRepository.save(user);
            return requestDto;
        }
        else{
           return null;
        }

    }

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
