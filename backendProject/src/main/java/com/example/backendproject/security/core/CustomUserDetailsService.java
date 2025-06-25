package com.example.backendproject.security.core;

import com.example.backendproject.user.entity.User;
import com.example.backendproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // 로그인 할 때 스프링에서 DB에 현재 로그인 하는 사용자가 존재하는지 확인하는 메서드
    @Override
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
        User user = userRepository.findByUserid(userid).orElseThrow(
                () -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다 ->  " + userid));
        return new CustomUserDetails(user);

    }


    public UserDetails loadUserById(Long userid) throws UsernameNotFoundException {
        User user = userRepository.findById(userid).orElseThrow(
                () -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다 ->  " + userid));
        return new CustomUserDetails(user);

    }
}
