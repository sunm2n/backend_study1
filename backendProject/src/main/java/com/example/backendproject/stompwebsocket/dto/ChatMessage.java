package com.example.backendproject.stompwebsocket.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private String message;
    private String from;

    private String to; // 귓속말을 받은 사람
    private String roomId; // 방 id

    public ChatMessage(String from, String message) {
        this.from = from;
        this.message = message;

    }
}
