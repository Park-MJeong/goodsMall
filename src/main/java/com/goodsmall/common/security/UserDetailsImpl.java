package com.goodsmall.common.security;

import com.goodsmall.common.util.EncryptionUtil;
import com.goodsmall.modules.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Slf4j
@Getter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    @Delegate
    private UserDto dto;

    private EncryptionUtil encryptionUtil;

    private Collection<? extends GrantedAuthority> authorities;


    @Override
    public String getPassword() {
        return dto.getPassword();
    }

    @Override
    public String getUsername() {
        return encryptionUtil.decrypt(dto.getUsername());
    }

    public String getUserEmail() {return encryptionUtil.decrypt(dto.getEmail());}
// 사용자의 권한 목록 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return authorities;
    }

    /**
     * 계정이 만료되지 않았는지 여부를 반환합니다.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    /**
     * 계정이 잠기지 않았는지 여부를 반환합니다.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    /**
     * 자격 증명(비밀번호)이 만료되지 않았는지 여부를 반환합니다.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    /**
     * 계정이 활성화되어 있는지 여부를 반환합니다.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}