package com.lianxi.kafka.controller;


import com.lianxi.core.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 文件请求处理
 *
 * @author ruoyi
 */
@RestController
@Slf4j
public class KafkaController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 发送消息
     *
     * @return
     */
    @GetMapping("/send")
    public R send(String message) {
        kafkaTemplate.send("testTopic", message);
        return R.ok();
    }

    /**
     * 发送消息
     *
     * @return
     */
    @GetMapping("/callback")
    public R callback(String message) {
        AtomicReference<String> msg = new AtomicReference<>("");
        kafkaTemplate.send("testTopic", message).addCallback(success -> {
            // 消息发送到的topic
            assert success != null;
            String topic = success.getRecordMetadata().topic();
            // 消息发送到的分区
            int partition = success.getRecordMetadata().partition();
            // 消息在分区内的offset
            long offset = success.getRecordMetadata().offset();
            msg.set("发送消息成功:" + topic + "-" + partition + "-" + offset);
        }, failure -> {
            msg.set("发送消息失败:" + failure.getMessage());
        });
        return R.ok(msg);
    }

}