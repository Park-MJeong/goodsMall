package com.goodsmall.common.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodsmall.common.api.ApiResponse;
import com.goodsmall.common.constant.ErrorCode;
import com.goodsmall.common.exception.BusinessException;
import com.goodsmall.common.security.CustomUserDetails;
import com.goodsmall.modules.user.dto.LoginUserRequestDto;
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

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
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
        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
//        String username = userDetails.getUsername();
        String email = userDetails.getEmail();
        Long userId = userDetails.getId();

        String token = jwtUtil.createAccessToken(userId,email);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER,token);

        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json; charset=UTF-8");
        ApiResponse<?> res = ApiResponse.success(token);
        response.getOutputStream().write(objectMapper.writeValueAsString(res).getBytes());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");
        jwtResponse(response,new BusinessException(ErrorCode.LOGIN_FAILED));
    }

    private void jwtResponse(HttpServletResponse response, BusinessException e) throws IOException {
        response.setContentType("application/json; charset=UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        ApiResponse<String> res = ApiResponse.createException(e.getCode(),e.getMessage());
        response.setStatus(e.getCode()); //응답헤더 설정
        response.getOutputStream().write(objectMapper.writeValueAsString(res).getBytes());
    }
}