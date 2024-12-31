package com.goodsmall.modules.user.service;

import com.goodsmall.common.api.ApiResponse;
import com.goodsmall.common.constant.ErrorCode;
import com.goodsmall.common.exception.BusinessException;
import com.goodsmall.common.util.EncryptionUtil;
import com.goodsmall.common.util.RandomCodeUtil;
import com.goodsmall.modules.user.domain.User;
import com.goodsmall.modules.user.dto.PasswordChangeRequestDto;
import com.goodsmall.modules.user.dto.UserRequestDto;
import com.goodsmall.modules.user.domain.UserRepository;
import com.goodsmall.modules.user.dto.EmailRequestDto;
import com.goodsmall.modules.user.dto.VerifyDto;
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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
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
        user.changePassword(bCryptPasswordEncoder.encode(requestDto.getPassword()));
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
        user.changePassword(requestDto.getConfirmPassword());
        userRepository.save(user);
        return ApiResponse.success("비밀번호가 변경되었습니다.");

    }


//    /**
//     *  로그인
//     */
//    public ApiResponse<?> login(LoginUserRequestDto requestDto, HttpServletResponse response) {
//        String userEmail = encryptionUtil.encrypt(requestDto.getEmail());
//
////        1. 입력받은 유저의 정보가 있는지 확인
//        Optional<User> loginUser = userRepository.findByEmail(userEmail);
//        if (loginUser.isEmpty()) {
//            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
//        }
//        Long userId = loginUser.get().getId();
//
////        2. 비밀번호 일치여부 확인
//        if (!bCryptPasswordEncoder.matches(requestDto.getPassword(), loginUser.get().getPassword())) {
//            throw new BusinessException(ErrorCode.LOGIN_FAILED);
//        }
////        3. 토큰 발급
//        String accessToken = JwtTokenProvider.createAccessToken(userId, userEmail);
//        String refreshToken;
//
//        // refresh 토큰의 유효시간이 24시간 미만이면 재발급
//        if(redisService.getRefreshTokenTTL(userId)<=EXPIRE_LIMIT || redisService.getRefreshTokenTTL(userId) == -2) {
//            refreshToken = JwtTokenProvider.generateRefreshToken(userId, loginUser.get().getUserName());
//            Long expiredTime = JwtTokenProvider.getExpirationTime(refreshToken);
//
////        레디스에 refreshToken 저장
//            redisService.saveRefreshToken(userId,refreshToken,expiredTime);
//        }else {
//            refreshToken = redisService.getRefreshToken(userId);
//        }
////        헤더에 accessToken 저장
//        response.addHeader(JwtTokenProvider.AUTHORIZATION_HEADER,accessToken);
//
//        // 5. 응답 데이터
//        Map<String, String> tokens = new HashMap<>();
//        tokens.put("accessToken", accessToken);
//        tokens.put("refreshToken", refreshToken);
//        return ApiResponse.success(tokens);
//
//    }
//
//    public ApiResponse<?> logout(String accessToken,Long userId) {
//        long expiredTime = JwtTokenProvider.getExpirationTime(accessToken);
//        if(expiredTime>0){
//            redisService.saveBlacklistToken(accessToken,expiredTime);
//        }
//        redisService.deleteRefreshToken(userId);
//        return ApiResponse.success("로그아웃 성공");
//    }



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
