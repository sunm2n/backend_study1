package com.example.backendproject.user.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import com.example.backendproject.user.dto.UserDTO;
import com.example.backendproject.user.dto.UserProfileDTO;
import com.example.backendproject.user.entity.User;
import com.example.backendproject.user.entity.UserProfile;
import com.example.backendproject.user.repository.UserProfilerRepository;
import com.example.backendproject.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {


    private final EntityManager em;

    private final UserRepository userRepository;
    private final UserProfilerRepository userProfilerRepository;


    /** 내 정보 조회 **/
    @Transactional(readOnly = true)
    public UserDTO getMyInfo(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. "));


        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUserid(user.getUserid());

        UserProfile profile = user.getUserProfile();

        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setUsername(profile.getUsername());
        profileDTO.setEmail(profile.getEmail());
        profileDTO.setPhone(profile.getPhone());
        profileDTO.setAddress(profile.getAddress());
        dto.setProfile(profileDTO);

        return dto;
    }


    /** 유저 정보 수정 **/
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO)  {
        User user = userRepository.findById(id)  //유저 레포지토리를 통해서 유저를 가져옴
                .orElseThrow(()-> new IllegalArgumentException("회원이 존재하지 않습니다. "));

        UserProfile profile = user.getUserProfile(); //프로필 객체를 만들어서 기존에 변경되기 전 프로필을 넣어줍니다.

        if (profile != null && userDTO.getProfile() != null) { //수정하려는 프로필이 있는지 체크 수정하려는 프로필정보가 있는지 체크
            UserProfileDTO dtoProfile = userDTO.getProfile();

            //프로필을  수정하기 위해 전달받은 데이터로 변경합니다.
            if (dtoProfile.getUsername() != null) profile.setUsername(dtoProfile.getUsername());
            if (dtoProfile.getEmail() != null) profile.setEmail(dtoProfile.getEmail());
            if (dtoProfile.getPhone() != null) profile.setPhone(dtoProfile.getPhone());
            if (dtoProfile.getAddress() != null) profile.setAddress(dtoProfile.getAddress());

        }
        /**
         * JPA에서 findById()로 가져온 엔티티는 영속 상태임.
         * 필드 값을 바꾸면 JPA가 트랜잭션 커밋할 때 자동으로 update 쿼리를 날림.
         * **/
        //아래는 변경된 내용을 프론트에 던져주기 위해 생성합니다.
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUserid(user.getUserid());
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setUsername(profile.getUsername());
        profileDTO.setEmail(profile.getEmail());
        profileDTO.setPhone(profile.getPhone());
        profileDTO.setAddress(profile.getAddress());
        dto.setProfile(profileDTO);
        return dto;
    }


    //아래는 순환참조가 되는  예제
    public User getProfile2(Long profileId) {
        UserProfile profile = userProfilerRepository.findById(profileId)
                .orElseThrow(()->new RuntimeException("프로필 없음"));

        return profile.getUser();
    }


    //dto로 순환참조 방지
    public UserDTO getProfile(Long profileId)  {
        UserProfile profile = userProfilerRepository.findById(profileId)
                .orElseThrow(()->new RuntimeException("프로필 없음"));

        User user =profile.getUser();
        if (user==null) throw new RuntimeException("연결된 유저 없음");

        UserProfileDTO profileDTO = new UserProfileDTO(
                profile.getUsername(),
                profile.getEmail(),
                profile.getPhone(),
                profile.getAddress()
        );

        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getUserid(),
                profileDTO
        );

        return userDTO;
    }



    @Transactional
    public void saveAllUsers(List<User> users) {
        long start = System.currentTimeMillis();

        for (int i = 0; i<users.size(); i++) {
            em.persist(users.get(i));
            if (i % 1000 == 0){
                em.flush();
                em.clear();
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("JPA saveAll 저장 소요 시간(ms): " + (end - start));
    }


}