package com.lianxi.IM.constants;

/**
 * <p>
 * websocket常量
 * </p>
 *
 * @author 福隆苑居士，公众号：【Java分享客栈】
 * @since 2022-04-02 10:11
 */
public class WsConstants {

	// stomp端点地址
	public static final String WEBSOCKET_PATH = "/websocket";

	// websocket前缀
	public static final String WS_PERFIX = "/app";

	// 消息订阅地址常量
	public static final class BROKER {
		// 点对点消息代理地址
		public static final String BROKER_QUEUE = "/queue/";
		// 广播消息代理地址
		public static final String BROKER_TOPIC = "/topic";
	}
}
