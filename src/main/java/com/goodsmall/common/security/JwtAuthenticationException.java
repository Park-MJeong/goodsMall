//package com.goodsmall.common.security;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class JwtAuthenticationException implements AuthenticationEntryPoint {
////    SpringSecurity 인증실패 예외
//    private final ObjectMapper objectMapper;
//
//    @Override
//    public void commence(HttpServletRequest request,
//                         HttpServletResponse response,
//                         AuthenticationException authException
//    ) throws IOException {
//        log.error("로그인 실패");
//        Map<String, String> errorResponse = new HashMap<>();
//        errorResponse.put("error", "Unauthorized");
//        errorResponse.put("message", "로그인 인증 실패");
//        response.setStatus(401);
//        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
//
//    }
//}
