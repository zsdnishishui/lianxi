package com.lianxi.redis.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = {"com.lianxi"})
public class RedisMQApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisMQApplication.class, args);
    }

}
