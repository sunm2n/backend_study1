package com.example.backendproject.oauth2;

import lombok.RequiredArgsConstructor;
import com.example.backendproject.Auth.entity.Auth;
import com.example.backendproject.Auth.repository.AuthRepository;
import com.example.backendproject.security.core.CustomUserDetails;
import com.example.backendproject.security.core.Role;
import com.example.backendproject.security.jwt.JwtTokenProvider;
import com.example.backendproject.user.entity.User;
import com.example.backendproject.user.entity.UserProfile;
import com.example.backendproject.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final AuthRepository authRepository;   // JWT 저장용 (access, refresh)
    private final UserRepository userRepository;   // 회원 정보 DB 접근
    private final JwtTokenProvider jwtTokenProvider; // JWT 발급기

    @Value("${jwt.accessTokenExpirationTime}")
    private Long jwtAccessTokenExpirationTime;
    @Value("${jwt.refreshTokenExpirationTime}")
    private Long jwtRefreshTokenExpirationTime;



    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {


        // 기본 OAuth2User 정보 가져오기 (email, name 등)
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 어떤 소셜 로그인 제공자인지 (google, kakao 등)
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // 소셜 사용자 정보 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String userid, username, email;


        // provider 별로 파싱 방식이 다름
        if ("google".equals(provider)) {

            email = (String) attributes.get("email");    // 구글의 고유 키
            userid = email;     // 사용자 아이디로 email 사용
            username = (String) attributes.get("name");

        } else if ("kakao".equals(provider)) {

            userid = attributes.get("id").toString()+"@kakao";   // 고유 id + 구분자
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            email = (String) kakaoAccount.get("email");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            username = (String) profile.get("nickname");

        } else {
            // 기타 provider 처리 안함
            email = null;
            username = null;
            userid = null;

        }

        System.out.println(provider+" 로그인 확인 userid = "+userid);
        System.out.println(provider+" 로그인 확인 email = "+email);
        System.out.println(provider+" 로그인 확인 username = "+username);

        // 회원 정보가 DB에 존재하는지 확인
        User user = userRepository.findByUserid(userid)
                .orElseGet(() -> {
                    // 회원이 없다면 자동 회원가입 처리
                    User newUser = new User();
                    newUser.setUserid(userid);
                    newUser.setPassword("");    // 소셜 로그인은 비밀번호 없음
                    newUser.setRole(Role.ROLE_USER);     // 기본 권한 설정


                    // 프로필 엔티티 생성 및 양방향 관계 설정
                    UserProfile profile = new UserProfile();
                    profile.setUsername(username != null ? username : "소셜유저");
                    profile.setEmail(email != null ? email : "");
                    profile.setPhone("");
                    profile.setAddress("");
                    profile.setUser(newUser);          //  주인 쪽 설정
                    newUser.setUserProfile(profile);   //  양방향 설정

                    return userRepository.save(newUser);   // 회원가입 저장
                });

        // 시큐리티에서 사용할 인증 객체 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        customUserDetails,
                        user.getPassword(),
                        Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())));


        // JWT 액세스 & 리프레시 토큰 발급
        String accessToken = jwtTokenProvider.generateToken(authentication,jwtAccessTokenExpirationTime);
        String refreshToken = jwtTokenProvider.generateToken(authentication,jwtRefreshTokenExpirationTime);


        // JWT는 SuccessHandler에서 쿠키/쿼리로 전달 → 여기선 속성에만 담아 둠
        Map<String, Object> customAttributes = new HashMap<>(attributes);
        customAttributes.put("accessToken", accessToken);
        customAttributes.put("refreshToken", refreshToken);
        customAttributes.put("id", user.getId()); // ← PK(id) 추가

        // Auth 엔티티에 토큰 저장 (User와 1:1 매핑)
        Optional<Auth> optionalAuth = authRepository.findByUser(user);
        if (optionalAuth.isPresent()) {
            Auth auth = optionalAuth.get();
            auth.updateAccessToken(accessToken);
            auth.updateRefreshToken(refreshToken);
            authRepository.save(auth); // 반드시 저장!
            user.setAuth(auth); // (option) 연관관계 유지
        } else {
            Auth auth = new Auth(user,"Bearer ",accessToken,refreshToken);
            authRepository.save(auth);
            user.setAuth(auth);
        }

        // 최종적으로 Spring Security에 전달할 OAuth2User 반환
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())), // 권한
                customAttributes,  // 속성 정보 (JWT 포함)
                "id" // PK로 사용할 식별자 (프론트에서도 사용할 수 있음)
        );
    }
}