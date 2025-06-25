package com.example.backendproject.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtKey {

    @Value("${jwt.secretKey}")
    private String secretKey;


    // 서명키를 만들어서 반환하는 메서드
    @Bean
    public SecretKey secretKey(){
        byte[] keyBytes = secretKey.getBytes(); // 설정파일에서 불러온 키 값을 바이트로 배열로 변환
        return new SecretKeySpec(keyBytes, "HmacSHA256"); // 바이트 배열을 HmacSHA256용 Security 객체로 매핑
    }
}
