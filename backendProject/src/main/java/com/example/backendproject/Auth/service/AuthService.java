package com.example.backendproject.Auth.service;

import com.example.backendproject.user.dto.UserProfileDTO;
import lombok.RequiredArgsConstructor;
import com.example.backendproject.Auth.dto.LoginRequestDTO;
import com.example.backendproject.Auth.dto.SignUpRequestDTO;
import com.example.backendproject.user.dto.UserDTO;
import com.example.backendproject.user.entity.User;
import com.example.backendproject.user.entity.UserProfile;
import com.example.backendproject.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;


    @Transactional
    public void signUp(SignUpRequestDTO dto){

        if (userRepository.findByUserid(dto.getUserid()).isPresent()){
            throw new RuntimeException("사용자가 이미 존재합나다.");
        }

        User user = new User();
        user.setUserid(dto.getUserid());
        user.setPassword(dto.getPassword());

        UserProfile profile = new UserProfile();
        profile.setUsername(dto.getUsername());
        profile.setEmail(dto.getEmail());
        profile.setPhone(dto.getPhone());
        profile.setAddress(dto.getAddress());

        /** 연관관계 설정 **/
        profile.setUser(user);
        user.setUserProfile(profile);

        userRepository.save(user);

    }



    public UserDTO
    login(LoginRequestDTO loginRequestDTO){
        User user = userRepository.findByUserid(loginRequestDTO.getUserid())
                .orElseThrow(()->new RuntimeException("해당 유저를 찾을 수 없습니다."));

        if (!loginRequestDTO.getPassword().equals(user.getPassword())){
            throw new RuntimeException("비밀번호가 일치 하지 않습니다.");
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUserid(user.getUserid());


        // 유저 프로필
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setUsername(user.getUserProfile().getUsername());
        profileDTO.setEmail(user.getUserProfile().getEmail());
        profileDTO.setPhone(user.getUserProfile().getPhone());
        profileDTO.setAddress(user.getUserProfile().getAddress());

        return userDTO;

    }


}