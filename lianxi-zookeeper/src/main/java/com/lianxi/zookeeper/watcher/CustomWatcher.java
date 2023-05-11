package com.lianxi.zookeeper.watcher;


import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类文件: WatcherApi
 * <p>
 * <p>
 * 类描述：
 * <p>
 * 作     者： AusKa_T
 * <p>
 * 日     期： 2021/3/24 0024
 * <p>
 * 时     间： 9:40
 * <p>
 */
public class CustomWatcher implements Watcher {

    private static final Logger logger = LoggerFactory.getLogger(CustomWatcher.class);

    @Override
    public void process(WatchedEvent event) {
        logger.info("监听事件的状态: {}", event.getState());
        logger.info("监听事件的路径: {}", event.getPath());
        logger.info("监听事件的类型: {}", event.getType());
    }

}

