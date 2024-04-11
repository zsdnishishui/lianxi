package com.lianxi.pg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = {"com.lianxi"})
public class PGApplication {

    public static void main(String[] args) {
        SpringApplication.run(PGApplication.class, args);
    }

}
