package com.lianxi.IM;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = {"com.lianxi"})
public class IMApplication {

    public static void main(String[] args) {
        SpringApplication.run(IMApplication.class, args);
    }

}
