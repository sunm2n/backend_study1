package com.example.backendproject.Auth.Controller;

import com.example.backendproject.Auth.dto.LoginResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import com.example.backendproject.Auth.dto.LoginRequestDTO;
import com.example.backendproject.Auth.dto.SignUpRequestDTO;
import com.example.backendproject.Auth.service.AuthService;
import com.example.backendproject.user.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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





}