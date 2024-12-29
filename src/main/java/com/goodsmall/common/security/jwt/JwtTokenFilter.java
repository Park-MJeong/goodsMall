package com.goodsmall.common.security.jwt;

import com.goodsmall.common.security.CustomUserDetailsService;
import com.goodsmall.common.util.EncryptionUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final EncryptionUtil encryptionUtil;



//    OncePerRequestFilter를 상속받으면 HttpServletRequest, HttpServletResponse를 받아 올수 있음
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        //Authorization 헤더 검증
        if (authorizationHeader==null|| !authorizationHeader.startsWith("Bearer ")) {
            //조건이 해당되면 메소드 종료 ()
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.split(" ")[1].trim();

        if (!jwtUtil.isValidToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 응답
            response.getWriter().write("Token Expired");
            log.error("Token Error");
            return;
        }
        String userEmail = jwtUtil.getClaimsToUserEmail(token);

        try {
            setAuthentication(userEmail);
        } catch (Exception e) {
            log.error(e.getMessage());
            return;
        }
        filterChain.doFilter(request, response);
    }

    // 인증 처리
    public void setAuthentication(String userEmail) {
        String decryptEmail = encryptionUtil.decrypt(userEmail);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(decryptEmail);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String userEmail) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}