package com.lianxi.auth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityController {

    @GetMapping("/security")
    public String welcomeToSecurityPage() {

        return "欢迎学习Security,努力加油!";
    }

    @PreAuthorize("hasAuthority('sa:read:write')")
    @GetMapping("/hello")
    public String hello() {
        return "Hello,Security...";
    }

    @GetMapping("/success")
    public String success() {
        return "Welcome Login Success";
    }
}

