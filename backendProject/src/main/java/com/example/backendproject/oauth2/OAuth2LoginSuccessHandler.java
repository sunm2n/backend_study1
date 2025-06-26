package com.example.backendproject.oauth2;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {


        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        Map<String, Object> attributes = oAuth2User.getAttributes();


        String accessToken = (String) attributes.get("accessToken");
        String refreshToken = (String) attributes.get("refreshToken");
        String name = (String) attributes.get("name");

        System.out.println("[OAuth2_LOG]" + "소셯 로그인 시도한 이름 = "+name);

        // 사용자 ID를 안전하게 꺼내기 (null 체크 및 타입 캐스팅)
        Long id = null;
        Object idObj = attributes.get("id");
        if (idObj != null) {
            // Long 타입이 아닐 수도 있으니 안전하게 변환
            id = Long.valueOf(idObj.toString());
        }


        //토큰 전달방식
        // 또는, 보안을 강화하려면 아래처럼 HttpOnly 쿠키로 전달해도 됨
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        //  accessTokenCookie.setMaxAge(60 * 3); // 3분짜리 임시쿠키
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        // refreshTokenCookie.setMaxAge(60 * 60 * 24); // 1일짜리
        response.addCookie(refreshTokenCookie);


        response.sendRedirect("/main.html?" + "&id=" + id);
        //  response.sendRedirect("http://localhost:3000/main" + (id != null ? "?id=" + id : ""));//리액트
    }
}