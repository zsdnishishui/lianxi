package com.lianxi.IM.service;

import com.lianxi.IM.constants.WsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SocketServiceImpl implements SocketService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendMsg(String msg) {
        // 发送消息给客户端
        messagingTemplate.convertAndSend(WsConstants.BROKER.BROKER_TOPIC, msg);
    }

    @Override
    public void sendUserMsg(String userId, String msg) {
        // 发送消息给指定客户端
        messagingTemplate.convertAndSendToUser(userId, WsConstants.BROKER.BROKER_TOPIC, msg);
    }
}
