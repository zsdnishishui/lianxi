package com.lianxi.auth.service;


import com.lianxi.auth.enity.User;
import com.lianxi.auth.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getByUsername(String username) {
        return userMapper.getByUsername(username);
    }

    @Override
    public int addUser(User user) {
        return userMapper.addUser(user);
    }
}
