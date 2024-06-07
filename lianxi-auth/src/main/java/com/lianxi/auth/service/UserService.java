package com.lianxi.auth.service;


import com.lianxi.auth.enity.User;

public interface UserService {
    User getByUsername(String username);

    int addUser(User user);
}
