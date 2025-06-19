package com.example.backendproject.stompwebsocket.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import com.example.backendproject.stompwebsocket.dto.ChatMessage;
import com.example.backendproject.stompwebsocket.redis.RedisPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    //단일 브로드캐스트 (방을 동적으로 생성이 안됨)
//    @MessageMapping("/chat.sendMessage")
//    @SendTo("/topic/public")
//    public ChatMessage sendMessage(ChatMessage message){
//        return message;
//    }


    //서버가 클라이언트에게 수동으로 메세지를 보낼 수 있도록 하는 클래스
    private final SimpMessagingTemplate template;

    //동적으로 방 생성 가능
    @Value("${PROJECT_NAME:web Server}")
    private String instansName;

    private final RedisPublisher redisPublisher;
    private ObjectMapper objectMapper  = new ObjectMapper();

    @MessageMapping("/chat.sendMessage")
    public void sendmessage(ChatMessage message) throws JsonProcessingException {

        message.setMessage(instansName+" "+message.getMessage());

        String channel = null;
        String msg = null;

        if (message.getTo() != null && !message.getTo().isEmpty()) {
            // 귓속말
            //내 아이디로 귓속말경로를 활성화 함
            channel = "private."+message.getRoomId();
            msg = objectMapper.writeValueAsString(message);

        } else {
            // 일반 메시지
            channel = "room."+message.getRoomId();
            msg = objectMapper.writeValueAsString(message);
        }


        redisPublisher.publish(channel,msg);


    }

}