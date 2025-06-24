package com.example.backendproject.user.controller;


import lombok.RequiredArgsConstructor;
import com.example.backendproject.user.dto.UserDTO;
import com.example.backendproject.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user") //변경
@RequiredArgsConstructor
public class UserController {

    //삭제
//    @Value("${PROJECT_NAME:web Server}")
//    private String instansName;
//
//    @GetMapping
//    public String test(){
//        return instansName;
//    }

    private final UserService userService;

    /**
     * 내 정보 보기
     **/
    @GetMapping("/me/{id}")
    public ResponseEntity<UserDTO> getMyInfo(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(userService.getMyInfo(userId));
    }

    /**
     * 유저 정보 수정
     **/
    @PutMapping("/me/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") Long userId, @RequestBody UserDTO dto) {
        UserDTO updated = userService.updateUser(userId, dto);
        return ResponseEntity.ok(updated);
    }
}
