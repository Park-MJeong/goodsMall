package com.goodsmall.common.security;

import com.goodsmall.modules.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Slf4j
@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final String email;
    private final User.UserRoleEnum role;
    private final Collection<? extends GrantedAuthority> authorities;

    @Builder(toBuilder = true)
    public CustomUserDetails(Long id, String username, String password, String email, User.UserRoleEnum role, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.authorities = authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String authority = "ROLE_" + this.role.name();  // 예: "ROLE_USER", "ROLE_ADMIN"
        return Set.of(new SimpleGrantedAuthority(authority));
    }

    /**
     * 계정이 만료되지 않았는지 여부를 반환
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
