package com.example.backendproject.purewebsocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.backendproject.purewebsocket.dto.ChatMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    //세션을 관리하는 객체
    //Collections.synchronizedSet <- 여러 스레드가 동시에 이 객체에 접근할 때 동시설 문제를 안전하게 만들어주는 역할
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    //json 문자열 <-> 자바 객체 변환
    private final ObjectMapper objectMapper = new ObjectMapper();

    //방과 방 안에 있는 세션을 관리하는 객체
    private final Map<String,Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();



    //클라이언트가 웹소켓 서버에 접속했을 때 호출`
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        sessions.add(session);

        System.out.println("접속된 클라이언트 세션 ID = "+session.getId());
    }

    //클라이언트가 보낸 메세지를 서버가 받았을 떄 호출
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);

        //json 문자열 -> 자바 객체
        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);

        String roomID = chatMessage.getRoomId(); //클라이언트에게 받은 메세지에서 roomID를 추출

        if (!rooms.containsKey(roomID)){ //방을 관리하는 객체에 현재 세션이 들어가는 방이 있는지 확인
            rooms.put(roomID, ConcurrentHashMap.newKeySet()); //없으면 새로운 방을 생성
        }
        rooms.get(roomID).add(session); //해당 방에 세션 추가

        for (WebSocketSession s : rooms.get(roomID)){
            // for (WebSocketSession s : sessions) {
            if (s.isOpen()){
                //자바 객체 -> json 문자열
                s.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));

                System.out.println("전송된 메세지 = "+chatMessage.getMessage());
            }
        }

    }

    //클라이언트의 연결이 끊어졌을 때 호출
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);

        sessions.remove(session);

        //연결이 해제되면 소속되어있는 방에서 제거
        for (Set<WebSocketSession> room : rooms.values()){
            room.remove(session);
        }

    }
}