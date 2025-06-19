package com.example.backendproject.purewebsocket.handler;

import com.example.backendproject.purewebsocket.handler.dto.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.Session;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.http.WebSocket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    // json 문자열 <-> 자바 객체 변환
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 방과 방 안에 있는 세션을 관리하는 객체
    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();



    // 클라이언트가 웹소켓 서버에 접속했을 때
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        sessions.add(session); // 세션 등록 (접속한 클라이언트 추가)
        System.out.println("새 연결: " + session.getId());
    }

    // 클라이언트가 메세지를 보냈을 때
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {
        super.handleTextMessage(session, message);

        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);

        String roomID = chatMessage.getRoomID(); // 클라이언트에게 받은 메시지에서 roomID를 추출

        if (!rooms.containsKey(roomID)) { // 방을 관리하는 객체에 현재 세션이 들어가는 방이 있는지 확인
            rooms.put(roomID, ConcurrentHashMap.newKeySet()); // 없으면 새로운 방을 생성
        }
        rooms.get(roomID).add(session); // 해당 방에 세션 추가


        for (WebSocketSession s : rooms.get(roomID)) {
            // for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                //자바 객체 -> json 문자열
                s.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));

                System.out.println("전송된 메세지 = " + chatMessage.getMessage());
            }
        }

    }

    // 클라이언트 연결이 종료됐을 때
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessions.remove(session); // 세션 제거
        System.out.println("연결 종료: " + session.getId());
    }
}
