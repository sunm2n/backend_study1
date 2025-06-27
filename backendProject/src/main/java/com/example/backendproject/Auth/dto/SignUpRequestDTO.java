package com.example.backendproject.Auth.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDTO {

    private String userid;
    private String password;
    private String username;
    private String email;
    private String phone;
    private String address;

}
