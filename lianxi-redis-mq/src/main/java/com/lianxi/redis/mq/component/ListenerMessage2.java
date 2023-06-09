package com.lianxi.redis.mq.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class ListenerMessage2 implements StreamListener<String, MapRecord<String, String, String>> {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ?> streamMessageListenerContainerOptions;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        // 接收到消息
        System.out.println("message id " + message.getId());
        System.out.println("stream " + message.getStream());
        System.out.println("body " + message.getValue());

        // 消费完成后确认消费（ACK）
        redisTemplate.opsForStream(streamMessageListenerContainerOptions.getRequiredHashMapper()).acknowledge(message.getStream(), "group2", message.getId());
    }

}
