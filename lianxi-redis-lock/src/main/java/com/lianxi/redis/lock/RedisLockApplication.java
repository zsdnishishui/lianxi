package com.lianxi.redis.lock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = {"com.lianxi"})
public class RedisLockApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisLockApplication.class, args);
    }

}
