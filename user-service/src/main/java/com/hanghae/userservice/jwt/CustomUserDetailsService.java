package com.hanghae.userservice.jwt;


import com.hanghae.userservice.util.EncryptionUtil;
import com.hanghae.userservice.domain.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    /*사용자 인증시에 사용자 정보를 db에서 가져옴
    * email로 조회하고 조회된 사용자 정보를 userdetails 객체로 반환*/
    private final UserRepository repository;
    private final EncryptionUtil encryptionUtil;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String userEmail =encryptionUtil.encrypt(email);
        return repository.findByEmail(userEmail)
                .map(user -> CustomUserDetails.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .username(user.getUserName())
                        .password(user.getPassword())
                        .role(user.getRole())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
    }
}
