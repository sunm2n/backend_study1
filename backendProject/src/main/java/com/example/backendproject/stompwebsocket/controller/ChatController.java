package com.example.backendproject.stompwebsocket.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import com.example.backendproject.stompwebsocket.dto.ChatMessage;
import com.example.backendproject.stompwebsocket.gpt.GPTService;
import com.example.backendproject.stompwebsocket.redis.RedisPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    //서버가 클라이언트에게 수동으로 메세지를 보낼 수 있도록 하는 클래스
    private final SimpMessagingTemplate template;

    //환경 변수를 받아서 화면에 출력
    @Value("${PROJECT_NAME:web Server}")
    private String instansName;
    private final RedisPublisher redisPublisher;
    private final ObjectMapper objectMapper  = new ObjectMapper();

    private final GPTService  gptService;


    /** 클라이언트가 서버로 보낼 앤드포인트 **/
    //gpt ai 챗봇 앤드포인트
    @MessageMapping("/gpt")
    public void sendMessageGPT(ChatMessage message) throws Exception {

        template.convertAndSend("/topic/gpt",message);
        //내가 보낸 메세지 내 채팅방에 반환


        String getResponse = gptService.gptMessage(message.getMessage());
        //GPT API 요청을 위해 내가 보낸 메세지로 HTTP 요청 보냄


        ChatMessage chatMessage = new ChatMessage("난 GPT",getResponse);
        template.convertAndSend("/topic/gpt",chatMessage);
        //GPT API 응답을 내 채팅방에 반환

    }



    /** 클라이언트가 서버로 보낼 앤드포인트 **/
    // 단체 체팅방 앤드포인트
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessage message) throws JsonProcessingException {

        message.setMessage(instansName+" "+message.getMessage());

        String channel = null;
        String msg = null;

        if (message.getTo() != null && !message.getTo().isEmpty()) {
            // 귓속말
            channel = "private."+message.getRoomId();
            msg = objectMapper.writeValueAsString(message);

        } else {
            // 일반 메시지
            channel = "room."+message.getRoomId();
            msg = objectMapper.writeValueAsString(message);
        }

        redisPublisher.publish(channel,msg); //Redis에게 메세지 발행

    }
}