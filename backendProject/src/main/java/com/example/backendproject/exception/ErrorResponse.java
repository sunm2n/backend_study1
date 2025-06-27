package com.example.backendproject.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int code; // 상태코드
    private String message;
    private String detail;
}
