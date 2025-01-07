package com.hanghae.userservice.jwt;

import com.hanghae.common.api.ApiResponse;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.common.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.hanghae.userservice.jwt.dto.LoginUserRequestDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;
    private final Long EXPIRE_LIMIT = 24*60*60*1000L; // 24시간


    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, TokenRepository tokenRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenRepository = tokenRepository;
        setFilterProcessesUrl("/api/users/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
        try {
            LoginUserRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginUserRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.PASSWORD_FAILED);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");
//        String deviceId = request.getHeader("Device-Id");

        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
        String email = userDetails.getEmail();
        Long userId = userDetails.getId();

        String accessToken = jwtTokenProvider.createAccessToken(userId,email);

        String refreshToken;
        Long refreshExpire = tokenRepository.getRefreshTokenTTL(userId);
        if(refreshExpire <= EXPIRE_LIMIT || refreshExpire == -2){
            refreshToken = jwtTokenProvider.createRefreshToken(userId);
            long expiredTime = jwtTokenProvider.getExpirationTime(refreshToken);
            tokenRepository.saveRefreshToken(userId,refreshToken, expiredTime);
        }else {
            refreshToken = tokenRepository.getRefreshToken(userId);
        }

        response.addHeader(JwtTokenProvider.AUTHORIZATION_HEADER,accessToken);

        // 2. 응답 바디 데이터 구성
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        // 3. 응답 설정
        ObjectMapper objectMapper = new ObjectMapper();

        response.setContentType("application/json; charset=UTF-8");
        response.getOutputStream().write(
                objectMapper.writeValueAsString(ApiResponse.success(tokens)).getBytes()
        );

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");
        jwtResponse(response,new BusinessException(ErrorCode.LOGIN_FAILED));
    }

    private void jwtResponse(HttpServletResponse response, BusinessException e) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json; charset=UTF-8");

        ApiResponse<?> res = ApiResponse.createException(response.getStatus(),e.getMessage());
        response.setStatus(response.getStatus()); //응답헤더 코드설정
        response.getOutputStream().write(objectMapper.writeValueAsString(res).getBytes());
    }
}