package com.lianxi.redis.mq.configure;

public class Receiver {
    public void receiveMessage(String message) {
        System.out.println(message);
    }
}
