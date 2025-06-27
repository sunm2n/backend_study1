package com.example.backendproject.Auth.dto;

import com.example.backendproject.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exception")
public class TestController {


    @GetMapping
    private ResponseEntity<ErrorResponse> test() {

        throw new RuntimeException("테스트 중입니다");
    }
}
