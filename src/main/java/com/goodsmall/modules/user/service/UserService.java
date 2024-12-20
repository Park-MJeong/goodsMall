package com.goodsmall.modules.user.service;

import com.goodsmall.common.email.EmailService;
import com.goodsmall.common.redis.RedisService;
import com.goodsmall.common.security.EncryptionUtil.EncryptionService;
import com.goodsmall.modules.user.dto.UserRequestDto;
import com.goodsmall.modules.user.domain.User;
import com.goodsmall.modules.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final EmailService emailService;
    private final RedisService redisService;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;


    @Transactional
    public void signup(UserRequestDto requestDto) {
        log.info("회원가입: 유저이메일{} 인증코드{}",requestDto.getEmail(),requestDto.getCertifyCode());

        boolean checkData = redisService.checkData(requestDto.getEmail(), requestDto.getCertifyCode());
        if (checkData) {
            User user = new User(encriptUser(requestDto));
            userRepository.save(user);
        }
        else{
           throw  new IllegalArgumentException("인증번호가 일치하지않습니다.");
        }

    }

//    개인정보 암호화
    public UserRequestDto encriptUser(UserRequestDto requestDto) {
        String userName = encryptionService.encryptName(requestDto.getUserName());
        String phoneNumber = encryptionService.encryptPhone(requestDto.getPhoneNumber());
        String address = encryptionService.encryptAddress(requestDto.getAddress());
        String email = encryptionService.encryptEmail(requestDto.getEmail());
        String password = encryptionService.encryptPhone(requestDto.getPassword());
        return new UserRequestDto (userName,phoneNumber,address,email,password);
    }

    public boolean checkEmail(String email) {
        String userEmail = encryptionService.encryptEmail(email);
        log.info("암호화된 이메일{}",userEmail);
        return userRepository.findByEmail(userEmail).isPresent();
    }

    public void certifyEmail(String email) {
        log.info("유저가 입력한 이메일 {}",email);

        //0.입력한 이메일이 가입되어있는지 확인
        if(checkEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다");
        }
//        1. 메일을 발송
        emailService.sendEmail(email);
    }





}
