package com.security;

import com.domain.user.User;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Security User - 橋接 SecurityUserDetails 與 Domain User
 * <p>
 * 優點：
 * 1. 避免登入後的二次 DB 查詢
 * 2. 集中管理權限映射 (ROLE_ + PERMISSION)
 * 3. 方便在 Application 層直接獲取完整 Domain 物件
 */
public class SecurityUser implements UserDetails {

    @Getter
    @NonNull
    private final User domainUser;
    private final List<SimpleGrantedAuthority> authorities;

    public SecurityUser(@NonNull User user) {
        this.domainUser = Objects.requireNonNull(user, "Domain User cannot be null");
        // 集中映射規則：角色加 ROLE_ 前綴，權限點保持原樣
        this.authorities = Stream.concat(
                user.getRoleCodes().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)),
                user.getPermissions().stream().map(SimpleGrantedAuthority::new)
        ).collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return domainUser.getPassword();
    }

    @Override
    public String getUsername() {
        // 使用 email 作為 principal，若無則用 username
        return (domainUser.getEmail() != null && !domainUser.getEmail().isBlank()) 
            ? domainUser.getEmail() : domainUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return domainUser.getStatus() == com.domain.user.enums.UserStatus.ACTIVE;
    }
}
