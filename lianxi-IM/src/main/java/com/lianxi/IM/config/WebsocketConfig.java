package com.lianxi.IM.config;

import com.lianxi.IM.constants.WsConstants;
import com.lianxi.IM.interceptor.UserInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * <p>
 * websocket核心配置类
 * </p>
 * <p>
 * -- 说明：@EnableWebSocketMessageBroker作用
 * 1）、注解用于开启使用stomp协议，来传输基于代理（MessageBroker）的消息，消息代理配置在下面；
 * 2）、开始支持@MessageMapping，类似于@requestMapping，这样就可以在controller接口中配合@SendTo注解来传输消息了。
 *
 * @author 福隆苑居士，公众号：【Java分享客栈】
 * @since 2022/4/1 22:57
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 注册stomp端点
     * -- 说明：
     * 1）、addEndpoint：端点，简单理解就是连接websocket的后缀地址；
     * 2）、addInterceptors：拦截器，一般用来进行客户端认证；
     * 3）、setAllowedOrigins：跨域授权，必须加的选项，否则服务内部之间通讯因为端口问题也会出现跨域问题；
     * 4）、withSockJS：指定使用SockJS，一般spring的项目都使用这个，因为默认就支持，而SockJS是前端主流用法，优势是兼容性好，客户端不支持ws会降级为长轮询。
     *
     * @param registry stomp端点注册对象
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(WsConstants.WEBSOCKET_PATH)
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * 配置消息代理
     * -- 说明：
     * 1）、这里注释掉调度器和心跳配置，只作为参考，后续完整项目会加上；
     * 2）、enableSimpleBroker：配置客户端能订阅的域，可以多个，简单讲就是配置了这些域，客户端必须以这些名称作为订阅地址，否则访问不到服务端，
     * 如果不配置，客户端随便自定义路径都行，只要服务端有对应的接口提供订阅即可，一般是必须要设置的，大部分人喜欢设置为广播/topic，点对点/queue；
     * 3）、setUserDestinationPrefix：设置点对点的订阅路径前缀，不设置的话默认是/user，设置的话比如/queue，那么客户端订阅时就必须以/queue开头，否则无法访问服务端；
     * 4）、setApplicationDestinationPrefixes：设置应用级的订阅路径前缀，比如设置为/app，那么客户端订阅时就必须以/app开头，否则访问不到服务端。
     *
     * @param registry 消息代理注册对象
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 自定义调度器，控制心跳线程数。
//		ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
//		ts.setPoolSize(1);
//		ts.setThreadNamePrefix("ws-heartbeat-thread-");
//		ts.initialize();

        // 配置服务端推送消息给客户端的代理路径（客户端订阅的域），多个以逗号隔开。这里定义两个，点对点和广播代理。
        registry.enableSimpleBroker(WsConstants.BROKER.BROKER_QUEUE, WsConstants.BROKER.BROKER_TOPIC);
//				.setHeartbeatValue(new long[]{5000, 5000}) // 心跳5秒一次
//				.setTaskScheduler(ts); // 指定使用上面定义的调度器

        // 定义点对点推送时的前缀为/queue，默认是/user。加了后默认值就会被覆盖。
        registry.setUserDestinationPrefix(WsConstants.BROKER.BROKER_QUEUE);
        // 定义客户端访问服务端消息接口时的前缀，这里设为为/app， 默认是空字符串。加了之后前端访问接口就是stompClient.send("/app/send")。
        registry.setApplicationDestinationPrefixes(WsConstants.WS_PERFIX);
    }

    /**
     * 引入自定义配置
     *
     * @param registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(createUserInterceptor());
    }

    /**
     * 将stomp拦截器加入ioc容器
     *
     * @return
     */
    @Bean
    public UserInterceptor createUserInterceptor() {
        return new UserInterceptor();
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        // 消息大小
        registration.setMessageSizeLimit(500 * 1024 * 1024);
        // 缓冲大小
        registration.setSendBufferSizeLimit(1024 * 1024 * 1024);
        // 超时时间
        registration.setSendTimeLimit(200000);
    }
}
