package com.example.backendproject.stompwebsocket.config;


import com.example.backendproject.stompwebsocket.handler.CustomHandshakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration //이 클래스는 스프링의 설정 클래스라고 등록하는 어노테이션
@EnableWebSocketMessageBroker // Stomp 메시지 브로커 기능을 활성화하는 어노테이션
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /** Stomp에서 메세지가 어디로 전달될지 규칙(경로)을 정하는 곳   **/
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //Prefix <- 메세지의 목적지를 구분하기 위한 접두어

        /** 구독용 Prefix  **/

        // /topic  일반 채팅을 받을 접두어
        // /queue  귓속말을 받을 접두어
        /** 서버가 보내는 메시지를 클라이언트가 구독할 때 사용하는 경로 **/
        registry.enableSimpleBroker("/topic","/queue"); //구독용 경로



        /** 전송용 Prefix **/

        // 클라이언트가 서버에게 메세지를 보낼 접두어
        /**클라이언트가 서버에 메시지를 보낼 때 사용하는 경로 접두어   ->   @MessageMapping **/
        registry.setApplicationDestinationPrefixes("/app"); //  클라이언트 ->  서버

        // /user 특정 사용자에게 메세지를 보낼 접두어
        /** 서버가 특정 사용자에게 메시지를 보낼 때, 클라이언트가 구독할 경로 접두어 **/
        registry.setUserDestinationPrefix("/user"); // 서버 -> 특정 사용자

    }


    /** 클라이언트가 웹소켓에 연결할 엔드포인트를 등록 **/
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setHandshakeHandler(new CustomHandshakeHandler())
                .setAllowedOriginPatterns("*");

        registry.addEndpoint("/ws-gpt")
                .setAllowedOriginPatterns("*");

    }
}