package com.example.backendproject.stompwebsocket.redis;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

//@Profile("!test")
@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        //도커로 할떈 아래 반드시 해야 함
        configuration.setHostName(host);
        configuration.setPort(port);
        return new LettuceConnectionFactory(configuration); // localhost:6379로 기본 연결
    }
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
    /** 위쪽은 OAUth2 로그인용 **/





    /**  아래쪽은 웹소켁용 **/

    private final RedisSubscriber redisSubscriber;

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer Container = new RedisMessageListenerContainer();
        Container.setConnectionFactory(redisConnectionFactory);

        // 1. room.또는 private. 으로 시작되는 모든 채널은 일반 채팅 메세지 채널로 등록
        // 2. STOMP 서버가 클라이언트에게 메세지를 받으면 RedisPublisher을 통해서 room. 또는 private. 채널에게 메세지 발행
        // 3. room. 또는 private. 채널에 메세지가 발행되면 RedisSubscriber가 메세지를 수신하고 RedisSubscriber 클래스의 onMessage 함수 호출
        // 4. RedisSubscriber onMessage 안에서 메세지 내용을 파싱해서 roomId나 귓속말 대상을 구분하여 클라이언트에게 메세지를 전파

        Container.addMessageListener(new MessageListenerAdapter(redisSubscriber),new PatternTopic("room.*"));
        Container.addMessageListener(new MessageListenerAdapter(redisSubscriber), new PatternTopic("private.*"));

        return Container;

    }
}