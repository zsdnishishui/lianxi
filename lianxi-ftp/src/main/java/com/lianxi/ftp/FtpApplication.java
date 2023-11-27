package com.lianxi.ftp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = {"com.lianxi"})
public class FtpApplication {

    public static void main(String[] args) {
        SpringApplication.run(FtpApplication.class, args);
    }

}
