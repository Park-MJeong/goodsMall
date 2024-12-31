package com.hanghae.userservice.jwt;

import com.hanghae.common.util.EncryptionUtil;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;
    private final CustomUserDetailsService userDetailsService;
    private final EncryptionUtil encryptionUtil;



//    OncePerRequestFilter를 상속받으면 HttpServletRequest, HttpServletResponse를 받아 올수 있음
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        //Authorization 헤더 검증
        if (authorizationHeader==null|| !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = JwtTokenProvider.getHeaderToToken(authorizationHeader);
        System.out.println(authorizationHeader);

        if(tokenRepository.isBlacklisted(authorizationHeader)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token is blacklisted");
            log.error("블랙리스트에 등록된 토큰입니다.");
            return;
        }

        if (!jwtTokenProvider.isValidToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 응답
            response.getWriter().write("Token Expired");
            log.error("Token 만료");
            return;
        }
        String userEmail = JwtTokenProvider.getClaimsToUserEmail(token);

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