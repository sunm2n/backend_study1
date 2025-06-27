package com.example.backendproject.stompwebsocket.redis;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisPublisher {

    private final StringRedisTemplate stringRedisTemplate;

    public void publish(String channel,String message){
        stringRedisTemplate.convertAndSend(channel,message);
    }

}