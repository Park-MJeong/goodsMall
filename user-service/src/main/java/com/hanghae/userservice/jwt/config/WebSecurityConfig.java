package com.hanghae.userservice.jwt.config;

import com.hanghae.userservice.util.EncryptionUtil;
import com.hanghae.userservice.jwt.*;
import com.hanghae.userservice.service.RedisService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final EncryptionUtil encryptionUtil;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomLogoutHandler customLogoutHandler;
    private final TokenRepository tokenRepository;

    public WebSecurityConfig(JwtTokenProvider jwtTokenProvider, EncryptionUtil encryptionUtil, AuthenticationConfiguration authenticationConfiguration, CustomUserDetailsService customUserDetailsService, RedisService redisService, CustomLogoutHandler customLogoutHandler, TokenRepository tokenRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.encryptionUtil = encryptionUtil;
        this.authenticationConfiguration = authenticationConfiguration;
        this.customUserDetailsService = customUserDetailsService;
        this.tokenRepository = tokenRepository;
        this.customLogoutHandler = customLogoutHandler;
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider,tokenRepository);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }
    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter(jwtTokenProvider,tokenRepository,customUserDetailsService,encryptionUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 보안 사용 x
        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);

//        JWT토큰 사용 => 세션 사용 x
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//        URL별 접근 권한 설정
        http.authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/api/users/email-send").permitAll()
                        .requestMatchers("/api/users/email-verify").permitAll()
                        .requestMatchers("/api/users/signup").permitAll()
                        .requestMatchers("/api/products/**").permitAll()
                        .anyRequest().authenticated() //로그인한 사용자만 접근가능
        );
        // 로그아웃 설정
        http.logout(logout -> logout
                .logoutUrl("/api/users/logout") // 로그아웃 엔드포인트 설정
                .addLogoutHandler(customLogoutHandler)
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setContentType("text/plain;charset=UTF-8");
                    response.setCharacterEncoding("UTF-8");

                    // 로그아웃 성공 응답
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("로그아웃 성공");
                })
        );


//         필터 관리
//        addFilterBefore(a,b) b필터 전에 a필터를 먼저 수행(b 전에 a 를 위치하게 함)
        http.addFilterBefore(jwtTokenFilter(), JwtAuthenticationFilter.class); //JWT검증 필터
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); //로그인필터

        return http.build();
    }
}