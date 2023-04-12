package com.lianxi.rabbitMQ.rabbitListener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RabbitListener(queues = "direct_test")
@Slf4j
public class DirectReceiver {

    @RabbitHandler
    public void process(String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        try {
            //模拟出现异常
            //int a = 1 / 0;
            log.info("收到的消息-> msg:{}", msg);
            log.info("收到的消息标识-> deliveryTag:{}", deliveryTag);
            log.info("收到的消息-> channel:{}", channel.toString());
            //手动签收  channel.basicAck(消息唯一标识,是否批量签收);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            e.printStackTrace();
            //channel.basicNack(deliveryTag:消息的唯一标识,multiple:是否批量处理,requeue:是否重新放入队列);
            //消息出现异常时，若requeue=false，则该消息会被放入死信队列，若没有配置死信队列则该消息会丢失。
            channel.basicNack(deliveryTag, false, false);
        }
    }

}


