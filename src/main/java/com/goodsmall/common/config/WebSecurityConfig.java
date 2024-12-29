package com.goodsmall.common.config;

import com.goodsmall.common.security.CustomUserDetailsService;
import com.goodsmall.common.security.jwt.JwtTokenFilter;
import com.goodsmall.common.security.jwt.JwtUtil;
import com.goodsmall.common.security.jwt.JwtAuthenticationFilter;
import com.goodsmall.common.util.EncryptionUtil;
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
    private final JwtUtil jwtUtil;
    private final EncryptionUtil encryptionUtil;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomUserDetailsService customUserDetailsService;

    public WebSecurityConfig(JwtUtil jwtUtil, EncryptionUtil encryptionUtil, AuthenticationConfiguration authenticationConfiguration, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.encryptionUtil = encryptionUtil;
        this.authenticationConfiguration = authenticationConfiguration;
        this.customUserDetailsService = customUserDetailsService;
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
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }
    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter(jwtUtil, customUserDetailsService,encryptionUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 보안 사용 x
        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);

//        JWT토큰 사용 => 세션 사용 x
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/api/users/**").permitAll()
                        .anyRequest().authenticated() //로그인한 사용자만 접근가능
        );


//         필터 관리
//        addFilterBefore(a,b) b필터 전에 a필터를 먼저 수행(b 전에 a 를 위치하게 함)
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); //로그인필터
        http.addFilterAfter(jwtTokenFilter(), JwtAuthenticationFilter.class); //JWT검증 필터

        return http.build();
    }
}