package com.lianxi.redis.mq.configure;

import com.lianxi.redis.mq.component.ListenerMessage;
import com.lianxi.redis.mq.errorHandler.CustomErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.time.Duration;

/**
 * redis stream 配置（redis5.0以上）
 */
@Configuration
public class RedisStreamConfigs {

    @Autowired
    private ListenerMessage listenerMessage;

    @Autowired
    private ListenerMessage listenerMessage2;

    @Bean
    public StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ?> streamMessageListenerContainerOptions() {
        return StreamMessageListenerContainer
                .StreamMessageListenerContainerOptions
                .builder()
                // 一次最多获取多少条消息
                .batchSize(10)
                // 运行 Stream 的 poll task
//                .executor(executor)
                // 可以理解为 Stream Key 的序列化方式
                .keySerializer(RedisSerializer.string())
                // 可以理解为 Stream 后方的字段的 key 的序列化方式
                .hashKeySerializer(RedisSerializer.string())
                // 可以理解为 Stream 后方的字段的 value 的序列化方式
                .hashValueSerializer(RedisSerializer.string())
                // Stream 中没有消息时，阻塞多长时间，需要比 `spring.redis.timeout` 的时间小
                .pollTimeout(Duration.ofSeconds(1))
                // ObjectRecord 时，将 对象的 filed 和 value 转换成一个 Map 比如：将Book对象转换成map
//                .objectMapper(new ObjectHashMapper())
                // 获取消息的过程或获取到消息给具体的消息
                // 者处理的过程中，发生了异常的处理
                .errorHandler(new CustomErrorHandler())
                .build();
    }

    @Bean
    public StreamMessageListenerContainer streamMessageListenerContainer(RedisConnectionFactory factory,
                                                                         StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ?> streamMessageListenerContainerOptions) {
        StreamMessageListenerContainer listenerContainer = StreamMessageListenerContainer.create(factory,
                streamMessageListenerContainerOptions);
        listenerContainer.start();
        return listenerContainer;
    }

    /**
     * 独立消费
     *
     * @param streamMessageListenerContainer
     * @return
     */
    @Bean
    public Subscription single(StreamMessageListenerContainer streamMessageListenerContainer) {

        return streamMessageListenerContainer.receive(StreamOffset.fromStart("stream1"), listenerMessage);
    }

    /**
     * 订阅者1，消费组group1，收到消息后自动确认，与订阅者2为竞争关系，消息仅被其中一个消费
     * 需要用命令创建Stream类型的key和group
     *
     * @param streamMessageListenerContainer
     * @return
     */
    @Bean
    public Subscription subscription(StreamMessageListenerContainer streamMessageListenerContainer) {
        Subscription subscription = streamMessageListenerContainer.receiveAutoAck(
                Consumer.from("group1", "name1"),
                StreamOffset.create("stream1", ReadOffset.lastConsumed()),
                listenerMessage
        );
        return subscription;
    }

    /**
     * 订阅者2，消费组group1，收到消息后自动确认，与订阅者1为竞争关系，消息仅被其中一个消费
     *
     * @param streamMessageListenerContainer
     * @return
     */
    @Bean
    public Subscription subscription2(StreamMessageListenerContainer streamMessageListenerContainer) {
        Subscription subscription = streamMessageListenerContainer.receiveAutoAck(
                Consumer.from("group1", "name2"),
                StreamOffset.create("stream1", ReadOffset.lastConsumed()),
                listenerMessage
        );
        return subscription;
    }

    /**
     * 订阅者3，消费组group2，收到消息后不自动确认，需要用户选择合适的时机确认，与订阅者1和2非竞争关系，即使消息被订阅者1或2消费，亦可消费
     * <p>
     * 当某个消息被ACK，PEL列表就会减少
     * 如果忘记确认（ACK），则PEL列表会不断增长占用内存
     * 如果服务器发生意外，重启连接后将再次收到PEL中的消息ID列表
     *
     * @param streamMessageListenerContainer
     * @return
     */
    @Bean
    public Subscription subscription3(StreamMessageListenerContainer streamMessageListenerContainer) {
        Subscription subscription = streamMessageListenerContainer.receive(
                Consumer.from("group2", "name1"),
                StreamOffset.create("stream1", ReadOffset.lastConsumed()),
                listenerMessage2
        );
        return subscription;
    }

}
