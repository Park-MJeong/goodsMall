//package com.goodsmall.common.security.jwt;
//
//import com.goodsmall.common.security.UserDetailsServiceImpl;
//import io.jsonwebtoken.Claims;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Slf4j(topic = "JWT 검증 및 인가")
//@RequiredArgsConstructor
//public class JwtTokenFilter extends OncePerRequestFilter {
//    public static final String AUTHORIZATION_HEADER = "Authorization";
//    public static final String BEARER_PREFIX = "Bearer ";
//
//    private final TokenProvider tokenProvider;
//    private final UserDetailsServiceImpl userDetailsService;
//
//    public JwtAuthorizationFilter(TokenProvider tokenProvider, UserDetailsServiceImpl userDetailsService) {
//        this.tokenProvider = tokenProvider;
//        this.userDetailsService = userDetailsService;
//    }
//
////    OncePerRequestFilter를 상속받으면 HttpServletRequest, HttpServletResponse를 받아 올수 있음
//    @Override
//    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
//
//        String tokenValue = tokenProvider.getTokenFromRequest(req);
//
//        if (StringUtils.hasText(tokenValue)) {
//            // JWT 토큰 substring
//            tokenValue = tokenProvider.substringToken(tokenValue);
//            log.info(tokenValue);
//
//            if (!tokenProvider.validateToken(tokenValue)) {
//                log.error("Token Error");
//                return;
//            }
//
//            Claims info = tokenProvider.getUserInfoFromToken(tokenValue);
//
//            try {
//                setAuthentication(info.getSubject());
//            } catch (Exception e) {
//                log.error(e.getMessage());
//                return;
//            }
//        }
//
//        chain.doFilter(req, res);
//    }
//
//    // 인증 처리
//    public void setAuthentication(String username) {
//        SecurityContext context = SecurityContextHolder.createEmptyContext();
//        Authentication authentication = createAuthentication(username);
//        context.setAuthentication(authentication);
//
//        SecurityContextHolder.setContext(context);
//    }
//
//    // 인증 객체 생성
//    private Authentication createAuthentication(String username) {
//        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//    }
//}