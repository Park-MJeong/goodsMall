package com.goodsmall.common.security;

import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {
//    private final TokenProvider tokenProvider;
//    private final UserDetailsServiceImpl userDetailsService;
//    private final AuthenticationConfiguration authenticationConfiguration;
//
//    public WebSecurityConfig(TokenProvider tokenProvider, UserDetailsServiceImpl userDetailsService, AuthenticationConfiguration authenticationConfiguration) {
//        this.tokenProvider = tokenProvider;
//        this.userDetailsService = userDetailsService;
//        this.authenticationConfiguration = authenticationConfiguration;
//    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        return configuration.getAuthenticationManager();
//    }

//    @Bean
//    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
//        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenProvider);
//        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
//        return filter;
//    }
////
//    @Bean
//    public JwtAuthorizationFilter jwtAuthorizationFilter() {
//        return new JwtAuthorizationFilter(tokenProvider, userDetailsService);
//    }
//
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf(AbstractHttpConfigurer::disable);

//        http.authorizeHttpRequests((authorizeHttpRequests) ->
//                authorizeHttpRequests
//                        .requestMatchers(String.valueOf(PathRequest.toStaticResources().atCommonLocations())).permitAll()
//                        .requestMatchers("/api/users/authentication","api/users/signup","v3/api-docs").permitAll()
//                        .anyRequest().authenticated()
//        );


        // 로그인 사용
//        http.formLogin((formLogin) ->
//                formLogin
//                        // 로그인 View 제공 (GET /api/user/login-page)
//                        .loginPage("/api/user/login-page")
//                        .permitAll()
//        );
        // 필터 관리
//        addFilterBefore(a,b) b필터 전에 a필터를 먼저 수행(b 전에 a 를 위치하게 함)
//
//        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);// 로그인 전에 인가
//        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//
        return http.build();
    }
}