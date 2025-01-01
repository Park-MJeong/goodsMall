package com.hanghae.userservice.service;

import com.hanghae.common.api.ApiResponse;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.common.exception.BusinessException;
import com.hanghae.userservice.util.EncryptionUtil;
import com.hanghae.userservice.domain.User;
import com.hanghae.userservice.domain.UserRepository;
import com.hanghae.userservice.dto.EmailRequestDto;
import com.hanghae.userservice.dto.PasswordChangeRequestDto;
import com.hanghae.userservice.dto.UserRequestDto;
import com.hanghae.userservice.dto.VerifyDto;
import com.hanghae.userservice.util.RandomCodeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    private final RedisService redisService;
    private final MailService mailService;
    private final UserRepository userRepository;
    private final RandomCodeUtil randomCodeUtil;
    private final EncryptionUtil encryptionUtil;
    private final BCryptPasswordEncoder passwordEncoder;


    //    private final Long EXPIRE_LIMIT = 24*60*60*1000L; // 24시간
    private String encryptData(String data) {
        return encryptionUtil.encrypt(data);
    }

    private boolean checkEmail(String email) {
        String userEmail = encryptData(email);
        log.info("암호화된 이메일{}",userEmail);
        return userRepository.findByEmail(userEmail).isPresent();
    }

    /**
     *  db에 저장된 이메일확인후 메일 전숭
     */
    public ApiResponse<?> sendEmail(EmailRequestDto dto){
        String email=dto.getEmail();
        String msg ;
        //1.가입된 이메일인지 체크
        if(checkEmail(email)){
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if(redisService.isEmailExist(email)){msg="인증번호가 재전송 되었습니다.";}
        else msg= "인증번호가 전송 되었습니다.";

        //2.인증번호 생성
        String verifyCode = randomCodeUtil.createRandomCode();
        //3.레디스에 이메일,코드정보저장
        redisService.setData(email,verifyCode);
        //4. 이메일 전송
        mailService.sendEmail(email, verifyCode);
        return ApiResponse.success(msg);
    }

    /**
     *  레디스에 저장된 코드와 일치 확인
     */
    public ApiResponse<?> verifyEmail(VerifyDto dto){
        redisService.checkData(dto);
        return ApiResponse.success("이메일이 인증되었습니다");
    }

    /**
     *  회원가입
     */
    @Transactional
    public ApiResponse<?> signup(UserRequestDto requestDto) {
        String status = redisService.getStatus(requestDto.getEmail()); //이메일 인증상태
        String userEmail = encryptData(requestDto.getEmail());
        log.info(status);

        if(checkEmail(userEmail)){
            throw new BusinessException((ErrorCode.EMAIL_ALREADY_EXISTS));
        }

//        이메일 인증 확인
        if(status==null || !status.equals("true")){
            throw new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED);
        }
        log.info("회원가입: 유저이메일{}",requestDto.getEmail());

//        개인정보 암호화
        User user = new User(createEncryptedUser(requestDto));
        user.changePassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(user);

        return ApiResponse.success("회원가입이 완료되었습니다."+requestDto.getEmail());
    }

    public ApiResponse<?> changePassword(Long userId, PasswordChangeRequestDto requestDto) {
        User user =findUser(userId);
        if(!passwordEncoder.matches(requestDto.getCurrentPassword(),user.getPassword())){
            throw new BusinessException(ErrorCode.PASSWORD_CURRENT_ERROR);
        }
        if(!requestDto.getNewPassword().equals(requestDto.getConfirmPassword())){
            throw new BusinessException(ErrorCode.NEW_PASSWORD_ERROR);
        }
        if(passwordEncoder.matches(user.getPassword(),requestDto.getConfirmPassword())){
            throw new BusinessException(ErrorCode.INVALID_PASSWORD_CHANGE);
        }
        String password = passwordEncoder.encode(requestDto.getNewPassword());
        user.changePassword(password);
        userRepository.save(user);
        return ApiResponse.success("비밀번호가 변경되었습니다.");

    }

    private UserRequestDto createEncryptedUser (UserRequestDto requestDto) {
        UserRequestDto user = new UserRequestDto();
        user.setUserName(encryptData(requestDto.getUserName()));
        user.setPhoneNumber(encryptData(requestDto.getPhoneNumber()));
        user.setAddress(encryptData(requestDto.getAddress()));
        user.setEmail(encryptData(requestDto.getEmail()));
        return user;
    }

    public User findUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }



}
