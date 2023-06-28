package com.lianxi.IM.interceptor;

import com.lianxi.IM.enity.User;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

/**
 * @description: Stomp拦截器
 * @data: 2023/1/28
 */
@Component
public class UserInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        // 判断当前连接是否为stomp
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
            if (raw instanceof Map) {
                //获取token
                Object name = ((Map<?, ?>) raw).get("Authorization");
                if (name instanceof ArrayList) {
                    // 校验token
                    String username;
                    String token = ((ArrayList<?>) name).get(0).toString();
                    // 模拟校验规则
                    if (token.equals("abcdefg")) {
                        username = "张三";
                    } else if (token.equals("123456")) {
                        username = "李四";
                    } else {
                        throw new RuntimeException("websocket认证失败");
                    }
                    // 设置当前访问器的认证用户
                    accessor.setUser(new User() {{
                        setUserName(username);
                    }});
                } else {
//                    throw new RuntimeException("非法请求");
                    Object session = message.getHeaders().get("simpSessionId");
                    System.out.println(session);
                }
            }
        }
        return message;
    }
}
