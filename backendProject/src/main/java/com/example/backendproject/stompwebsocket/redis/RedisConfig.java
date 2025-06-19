package com.example.backendproject.stompwebsocket.redis;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {


    private final RedisSubscriber redisSubscriber;

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer Container = new RedisMessageListenerContainer();
        Container.setConnectionFactory(redisConnectionFactory);

        Container.addMessageListener(new MessageListenerAdapter(redisSubscriber),new PatternTopic("room.*"));

        return Container;

    }
}