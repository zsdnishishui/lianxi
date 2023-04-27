package com.lianxi.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消费者监听topic=testTopic的消息
 *
 * @author Lynch
 */
@Component
@Slf4j
public class ConsumerListener {

    //单条消费
    @KafkaListener(topics = "testTopic")
    public void processMessage(ConsumerRecord<?, ?> record, Acknowledgment ack) {
        try {
            System.out.printf("topic is %s, offset is %d,partition is %s, value is %s \n", record.topic(), record.offset(), record.partition(), record.value());
            // 手动提交offset
//            ack.acknowledge();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //批量消息
    @KafkaListener(topics = {"first_top"})
    public void consumerBatch(List<ConsumerRecord<?, ?>> records, Acknowledgment ack) {
        log.info("接收到消息数量：{}", records.size());
        //手动提交
//        ack.acknowledge();
    }

//    @KafkaListener(id = "consumer2",groupId = "felix-group", topics = "topic1")
//    public void onMessage3(List<ConsumerRecord<?, ?>> records) {
//        System.out.println(">>>批量消费一次，records.size()="+records.size());
//        for (ConsumerRecord<?, ?> record : records) {
//            System.out.println(record.value());
//        }
//    }

}
