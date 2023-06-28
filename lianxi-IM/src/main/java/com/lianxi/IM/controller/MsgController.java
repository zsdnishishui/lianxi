package com.lianxi.IM.controller;

import com.lianxi.IM.service.SocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 消息接口
 * </p>
 *
 * @author 福隆苑居士，公众号：【Java分享客栈】
 * @since 2022-04-02 12:00
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class MsgController {
    @Autowired
    private SocketService socketService;

    /**
     * 发送广播消息
     * -- 说明：
     * 1）、@MessageMapping注解对应客户端的stomp.send('url')；
     * 2）、用法一：要么配合@SendTo("转发的订阅路径")，去掉messagingTemplate，同时return msg来使用，return msg会去找@SendTo注解的路径；
     * 3）、用法二：要么设置成void，使用messagingTemplate来控制转发的订阅路径，且不能return msg，个人推荐这种。
     *
     * @param msg 消息
     */
    @MessageMapping("/send")
    public void sendAll(@RequestParam String msg) {

        log.info("[发送消息]>>>> msg: {}", msg);
        socketService.sendMsg(msg);

    }

    /**
     * 发送点对点消息（本案例没有用到，留作大家参考，后续完整的新项目会加入进来。）
     * -- 说明：
     * 1）、参考convertAndSendToUser源码，可以发现点对点发消息最终的订阅路径是： /user/{客户端id}/{消息订阅id}的形式，
     * 而我们在WebsocketConfig中设置了setUserDestinationPrefix为/queue，最终订阅路径就变成: /queue/{客户端id}/{消息订阅id}
     * 所以客户端页面send时要传的URL也得拼接成这种形式，否则发不过来。
     *
     * @param map 消息体
     */
    @MessageMapping("/sendToUser")
    public void sendToUser(Map<String, String> map) {

        log.info("[发送消息]>>>> map: {}", map);

        String fromUserId = map.get("fromUserId");
        String toUserId = map.get("toUserId");
        String msg = "来自" + fromUserId + "的消息:" + map.get("msg");
        socketService.sendUserMsg(toUserId, msg);

    }
}
