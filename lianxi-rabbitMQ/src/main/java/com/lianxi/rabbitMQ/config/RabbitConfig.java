package com.lianxi.rabbitMQ.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public final static String DIRECT_TEST = "direct_test";
    public final static String TOPIC_TEST = "topic_test";
    public final static String FANOUT_TEST = "fanout_test";
    public final static String DIRECT_EXCHANGE = "direct_exchange";
    public final static String DIRECT_ROUTER_KEY = "study";
    public final static String TOPIC_EXCHANGE = "topic_exchange";
    public final static String TOPIC_ROUTER_KEY = "topic.one";
    public final static String FANOUT_EXCHANGE = "fanout_exchange";

    @Bean
    public Queue directTest() {
        return new Queue(DIRECT_TEST);
    }

    @Bean
    public Queue topicTest() {
        return new Queue(TOPIC_TEST);
    }

    @Bean
    public Queue fanoutTest() {
        return new Queue(FANOUT_TEST);
    }

    @Bean
    DirectExchange direct_exchange() {
        return new DirectExchange(DIRECT_EXCHANGE);
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    Binding bindingDirectExchange(Queue directTest, DirectExchange exchange) {
        return BindingBuilder.bind(directTest).to(exchange).with(DIRECT_ROUTER_KEY);
    }

    @Bean
    Binding bindingTopicExchange(Queue topicTest, TopicExchange exchange) {
        return BindingBuilder.bind(topicTest).to(exchange).with(TOPIC_ROUTER_KEY);
    }

    @Bean
    Binding bindingExchange(Queue fanoutTest, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(fanoutTest).to(fanoutExchange);
    }
}


