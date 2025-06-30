package com.example.backendproject.security.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.backendproject.security.core.CustomUserDetailsService;
import com.example.backendproject.threadlocal.TraceIdHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    //JwtTokenFilter 모든 HTTP요청을 가로채서 JWT 토큰을 검사하는 필터 역할
    //OncePerRequestFilter는 한 요청당 딱 한번만 실행되는 필터 역할

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;



    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        //   log.info("path : {}", path);

        // 정적 파일 경로, 필터
        return path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.equals("/")
                || path.equals("/index.html")
                || path.endsWith(".html")
                || path.startsWith("/favicon.ico")
                || path.startsWith("/actuator/prometheus")
                || path.startsWith("/api/auth/");

        /**
         config에서 인증하라고하고 여기서 인증 무시하라고 하면 401 에러남
         **/
    }

    // HTTP 매 요청마다 호출
    @Override
    protected void doFilterInternal(HttpServletRequest request, //http 요청
                                    HttpServletResponse response, //http 응답
                                    FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            //HTTP 요청이 시작되는 구간에서 TraceID 발급
            String traceId = UUID.randomUUID().toString().substring(0, 8); //
            TraceIdHolder.set(traceId); //TraceID ThreadLocal에 저장

            String accessToken = getTokenFromRequest(request); //요청 헤더에서 토큰 추출

            if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
                UsernamePasswordAuthenticationToken authenticationToken =getAuthentication(accessToken);
                //토큰에서 사용자를 꺼내서 담은 사용자 인증 객체
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //http요청으로부터 부가 정보(ip,세션 등)를 추출해서 사용자 인증 객체에 넣어줌
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                //토큰에서 사용자 인증정보를 조회해서 인증정보를  현재 스레드에 인증된 사용자로 등록

                String url = request.getRequestURI().toString();
                String method = request.getMethod(); // "GET, POST, PUT"

                //   log.error("현재 들어온 HTTP 요청 = {}",url);
                //  log.error("✅ 토큰 인증 성공:{} " ,accessToken);

            }
            else {
                // log.info("❌ 토큰 없음 또는 유효하지 않음: {}" ,accessToken);
            }



            /**

             CharacterEncodingFilter: 문자 인코딩 처리
             CorsFilter: CORS 정책 처리
             CsrfFilter: CSRF 보안 처리
             JWTTokenFilter: JWT 토큰 처리(핵심)
             SecurityContextFilter: 인증/인가 정보 저장
             ExceptionFilter: 예외 처리

             * **/
            filterChain.doFilter(request,response); // JwtTokenFilter를 거치고 다음 필터로 넘어감
        }
        finally {
            //HTTP 요청이 끝날 때 ThreadLocal 데이터를 비워줌
            TraceIdHolder.clear();
            String afterClear = TraceIdHolder.get();
            log.info("TraceIdHolder 데이터 확인 : {}", afterClear);
        }

    }

    //HTTP 요청 헤더에서 토큰을 추출하는 메서드
    public String getTokenFromRequest(HttpServletRequest request) {

        String token = null;

        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
        }
        return token;
    }




    //http 요청에서 사용자 인증 정보를 담는 객체
    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        //JWT 토큰에서 사용자 id 추출
        Long userid = jwtTokenProvider.getUserIdFromToken(token);
        //위 추출한 id를 DB에서 사용자 정보 조회
        UserDetails userDetails = customUserDetailsService.loadUserById(userid);
        return new UsernamePasswordAuthenticationToken(
                userDetails, //사용자 정보
                null, //credential을 담는 부분 -> 이미 인증이 되었기 때문에 null
                userDetails.getAuthorities() //사용자의 권한
        );
    }





    /// ////


    // HTTP 요청 헤더에서 토큰을 추출하는 메서드
    private String extractTokenFromRequest(HttpServletRequest request) {
        // 1. 쿠키에서 accessToken 확인
        String cookieToken = extractTokenFromCookie(request);
        if (StringUtils.hasText(cookieToken)) {
            return cookieToken;
        }

        // 2. Authorization 헤더에서 토큰 확인
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 제거
        }

        return null;
    }

    // 쿠키에서 accessToken을 추출하는 메서드
    private String extractTokenFromCookie(HttpServletRequest request) {
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }












}