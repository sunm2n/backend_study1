package com.example.backendproject.Auth.Controller;

import com.example.backendproject.Auth.dto.LoginResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import com.example.backendproject.Auth.dto.LoginRequestDTO;
import com.example.backendproject.Auth.dto.SignUpRequestDTO;
import com.example.backendproject.Auth.service.AuthService;
import com.example.backendproject.user.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /** 회원가입 **/
    @PostMapping("/signUp")
    public ResponseEntity<String> singUp(@RequestBody SignUpRequestDTO signUpRequestDTO){
        try {
            authService.signUp(signUpRequestDTO);
            return ResponseEntity.ok("회원가입 성공");
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage()); //401
        }
    }

    /** 로그인 **/
    @PostMapping("/loginSecurity")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO){
        LoginResponseDTO loginResponseDTO = authService.login(loginRequestDTO);
        // UserDTO loginUser = authService.login(loginRequestDTO);
        return ResponseEntity.ok(loginResponseDTO);


    }

    /** 토큰 갱신 API */
    //refresh HTTP 요청 헤더에서 토큰을 추출하고 그 토큰으로 리프레시 토큰을 발급
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader(value = "Authorization", required = false)
                                          String authorizationHeader, HttpServletRequest request){
        String refreshToken = null;
        // 1. 쿠키에서 찾기
        if(request.getCookies() != null){
            for (Cookie cookie : request.getCookies()) {
                if("refresh_token".equals(cookie.getName())){
                    refreshToken = cookie.getValue();
                }
            }
        }
        // 2. Authorization 헤더 찾기
        if(refreshToken == null && authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
        refreshToken = authorizationHeader.replace("Bearer ", "").trim();
        }
        if(refreshToken == null && refreshToken.isEmpty()){
            throw new IllegalArgumentException("리프레시 토큰이 없습니다.");
        }
        String newAccessToken = authService.refreshToken(refreshToken);
        //json 객체로 변환하여 front에 내려주기
        Map<String, String> res = new HashMap<>();
        res.put("access_token", newAccessToken);
        res.put("refresh_token", refreshToken);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }





}