package com.lianxi.rabbitMQ;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = {"com.lianxi"})
public class RebbitMQApplication {

    public static void main(String[] args) {
        SpringApplication.run(RebbitMQApplication.class, args);
    }

}
