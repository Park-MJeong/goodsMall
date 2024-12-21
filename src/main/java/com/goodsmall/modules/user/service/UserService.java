package com.goodsmall.modules.user.service;

import com.goodsmall.common.email.EmailService;
import com.goodsmall.common.exception.ErrorCode;
import com.goodsmall.common.exception.ErrorException;
import com.goodsmall.common.redis.RedisService;
import com.goodsmall.common.security.EncryptionUtil.EncryptionService;
import com.goodsmall.modules.user.dto.EmailRequestDto;
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
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;
    private final RedisService redisService;

    @Transactional
    public void signup(UserRequestDto requestDto) {
        String status = redisService.getStatus(requestDto.getEmail()); //이메일 인증상태
        log.info(status);

        assert status != null;
        if(!status.equals("true")){
            throw new ErrorException(ErrorCode.EMAIL_NOT_VERIFIED);
        }
        if(checkEmail(requestDto.getEmail())){
            throw new ErrorException(ErrorCode.EMAIL_ALREADY_EXISTS);
        };

        log.info("회원가입: 유저이메일{} 인증코드{}",requestDto.getEmail(),requestDto.getVerifyCode());
            User user = new User(encriptUser(requestDto));
            userRepository.save(user);
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

    public void verifyEmail(EmailRequestDto dto) {

        //0.입력한 이메일이 가입되어있는지 확인
        if(checkEmail(dto.getEmail())) {
            throw new ErrorException(ErrorCode.EMAIL_ALREADY_EXISTS);}
//        1. 메일을 발송
        emailService.sendEmail(dto.getEmail());
    }





}
