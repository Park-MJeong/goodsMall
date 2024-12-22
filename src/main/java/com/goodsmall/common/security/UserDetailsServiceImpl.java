//package com.goodsmall.common.security;
//
//import com.goodsmall.common.security.EncryptionUtil.EncryptionService;
//import com.goodsmall.modules.user.domain.UserRepository;
//import com.goodsmall.modules.user.dto.UserDto;
//import lombok.AllArgsConstructor;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//@AllArgsConstructor
//public class UserDetailsServiceImpl implements UserDetailsService {
//    private final UserRepository repository;
//    private final EncryptionService encryptionService;
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        String userEmail = encryptionService.decryptEmail(email);
//        return repository.findByEmail(userEmail)
//                .map(user -> UserDto.builder()
//                        .username(user.getUserName())
//                        .password(user.getPassword())
//                        .email(user.getEmail())
//                        .build())
//                        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
//    }
//}
