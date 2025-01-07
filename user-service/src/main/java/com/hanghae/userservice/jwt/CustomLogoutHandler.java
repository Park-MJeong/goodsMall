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

        String authorizationHeader = request.getHeader("Authorization");
        String token = JwtTokenProvider.getHeaderToToken(authorizationHeader);

        if (token != null) {
            long expiredTime = jwtTokenProvider.getExpirationTime(authorizationHeader);
            Long userId= jwtTokenProvider.getClaimsToUserId(token);
            if(expiredTime>0){
                tokenRepository.addToBlacklist(authorizationHeader,expiredTime);
            }
            tokenRepository.deleteRefreshToken(userId);
        }
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