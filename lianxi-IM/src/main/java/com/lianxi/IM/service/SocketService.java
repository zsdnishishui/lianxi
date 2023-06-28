package com.lianxi.IM.service;

public interface SocketService {
    void sendMsg(String msg);

    void sendUserMsg(String userId, String msg);
}
