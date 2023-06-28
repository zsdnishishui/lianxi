package com.lianxi.IM.interceptor;

import com.lianxi.IM.service.SocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * 可以监听到websocket的断开
 */
@Component
@Slf4j
public class STOMPDisconnectEventListener implements ApplicationListener<SessionDisconnectEvent> {
    @Autowired
    private SocketService socketService;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        log.info("[发送消息]>>>> msg: {}", event.getSessionId() + "离开房间了");
        // 给所有的客户端发离开消息
        socketService.sendMsg("{\"info\":\"" + event.getSessionId() + "离开房间\",\"type\":2}");
        // 只给JackyLove的客户端发离开消息
        socketService.sendUserMsg("JackyLove", "{\"info\":\"" + event.getSessionId() + "离开房间\",\"type\":2}");
        //event.getSessionId();
        // event.getUser();
    }
}
