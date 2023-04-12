package com.lianxi.rabbitMQ.controller;


import com.lianxi.core.domain.R;
import com.lianxi.rabbitMQ.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 文件请求处理
 *
 * @author ruoyi
 */
@RestController
@Slf4j
public class RabbitMQController {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    /**
     * 发送消息
     *
     * @return
     */
    @GetMapping("/send")
    public R join(String message) {
        String context = message + new Date();
//        this.rabbitTemplate.convertAndSend("hello", context);
        this.rabbitTemplate.convertAndSend(RabbitConfig.DIRECT_EXCHANGE, RabbitConfig.DIRECT_ROUTER_KEY, context);
        return R.ok();
    }

}