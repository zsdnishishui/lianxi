package com.lianxi.auth;

import com.lianxi.auth.enity.User;
import com.lianxi.auth.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
public class AppTest {
    @Autowired
    private UserService userService;


    @Test
    public void testAddUser() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword(new BCryptPasswordEncoder().encode("123456"));
        user.setPhone("13688888888");
        System.out.println(user);
        userService.addUser(user);
    }
}
