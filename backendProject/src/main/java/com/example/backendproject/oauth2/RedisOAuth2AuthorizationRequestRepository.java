package com.example.backendproject.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.time.Duration;


@RequiredArgsConstructor
public class RedisOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    // Redis에 저장할 때 사용할 key prefix (고유 식별자 역할)
    private static final String PREFIX = "oauth2_auth_request:";

    // RedisTemplate은 Spring에서 제공하는 Redis 클라이언트
    private final RedisTemplate<String, Object> redisTemplate;

//    public RedisOAuth2AuthorizationRequestRepository(RedisTemplate<String, Object> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }


    //인가 요청 불러오는 메서드
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        // 요청 파라미터에서 state 값을 가져옴 (OAuth2의 CSRF 방지 토큰 역할)
        String state = request.getParameter("state");
        if (state == null) return null;

        // Redis에서 해당 state 값을 가진 AuthorizationRequest 조회
        return (OAuth2AuthorizationRequest) redisTemplate.opsForValue().get(PREFIX + state);
    }



    //완료된 인가 요청 저장
    @Override
    public void saveAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest,
            HttpServletRequest request,
            HttpServletResponse response) {

        // null일 경우 저장하지 않음
        if (authorizationRequest == null) return;
        // 고유 식별자인 state 값을 키로 사용
        String state = authorizationRequest.getState();
        // Redis에 10분동안 저장(10분 뒤 삭제됨)
        redisTemplate.opsForValue().set(
                PREFIX +
                        state,
                authorizationRequest,
                Duration.ofMinutes(10)); //인증된 정보를 10분동안 유지

    }

    /**
     * [3] 인가 요청 삭제하기
     * 인증 과정이 끝나면 Redis에서 해당 요청 정보를 제거함
     */
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(
            HttpServletRequest request,
            HttpServletResponse response) {
        String state = request.getParameter("state");
        if (state == null) return null;
        String key = PREFIX + state;

        // Redis에서 해당 요청을 가져오고
        OAuth2AuthorizationRequest authRequest = (OAuth2AuthorizationRequest) redisTemplate.opsForValue().get(key);

        // 가져온 후 삭제 (한 번 사용 후 만료되므로)
        redisTemplate.delete(key);
        return authRequest;
    }
}