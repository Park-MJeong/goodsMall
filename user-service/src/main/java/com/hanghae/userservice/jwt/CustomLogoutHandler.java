package com.hanghae.userservice.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public CustomLogoutHandler(TokenRepository tokenRepository, JwtTokenProvider jwtTokenProvider) {
        this.tokenRepository = tokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.debug("[+] 로그아웃이 수행이 됩니다.");

        // [STEP1] 요청 값에서 토큰을 추출합니다.
        String authorizationHeader = request.getHeader("Authorization");
        String token = JwtTokenProvider.getHeaderToToken(authorizationHeader);
//        String deviceId = request.getHeader("Device-Id");

        // [STEP2-1] 토큰이 존재하는 경우
        if (token != null) {
            long expiredTime = jwtTokenProvider.getExpirationTime(authorizationHeader);
            Long userId= jwtTokenProvider.getClaimsToUserId(token);
            if(expiredTime>0){
                tokenRepository.addToBlacklist(authorizationHeader,expiredTime);
            }
            tokenRepository.deleteRefreshToken(userId);
        }
        // [STEP2-2] 토큰이 존재하지 않는 경우
        else {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("userInfo", null);
            resultMap.put("resultCode", 9999);
            resultMap.put("failMsg", "로그아웃 과정에서 문제가 발생하였습니다.");

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = null;
            PrintWriter printWriter = null;
            try {
                jsonResponse = objectMapper.writeValueAsString(resultMap);
                printWriter = response.getWriter();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            printWriter.print(jsonResponse);
            printWriter.flush();
            printWriter.close();


        }
    }
}