package com.example.backendproject.security.core;

import com.example.backendproject.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.aspectj.apache.bcel.generic.LineNumberGen;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    // UserDetails <- 사용자 정보를 담는 인터페이스
    // 로그인한 사용자의 정보를 담아두는 역할

    private final User user;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // User의 권한을 반환하는 메서드
        // Collections.singleton <- 이 사용자는 한가지 권한만 갖는다는 의미
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()));
    }

    // 토큰에서 추출한 사용자 정보의 Id를 반환 (테이블의 pk 값)
    // User 엔티티에서 ID 추출
    public Long getId() {
        return user.getId();
    }


    @Override
    public String getPassword() {
        return user.getPassword(); // User 엔티티에서 password 반환
    }

    @Override
    public String getUsername() {
        return user.getUserid(); // User 엔티티와 참조되어있는 UserProfile에 있는 username 반환
    }


    /** 아래는 현재 계정 상태를 판단하는 메서드 **/
    @Override // 현재 계정 상태가 활성화인지
    public boolean isEnabled() {
        return true;
    }

    @Override // 이 계정이 만료되었는지
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override // 이 계정이 잠겨있는지
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override // 자격증명이 만료되지 않았는지
    public boolean isCredentialsNonExpired() {
        return true;
    }

}
