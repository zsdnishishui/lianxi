package com.lianxi.zookeeper;

import com.lianxi.zookeeper.util.ZkUtil;
import com.lianxi.zookeeper.watcher.CustomWatcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class ApplicationTest {

    @Autowired
    private ZkUtil zkUtil;

    /**
     * 新增节点
     */
    @Test
    public void testCreateNode() {
        zkUtil.createPerNode("/demo", "auskat");
    }

    /**
     * 修改节点
     */
    @Test
    public void testUpdateNode() {
        zkUtil.updateNode("/demo", "auskat-2");
    }

    /**
     * 获取节点是否存在
     * 自定义监听
     */
    @Test
    public void exists() {
        zkUtil.exists("/demo", new CustomWatcher());
    }

    /**
     * 获取节点数据
     * 自定义监听
     */
    @Test
    public void getData() throws InterruptedException {
        String data = zkUtil.getData("/demo", new CustomWatcher());
        System.out.println(data);
        zkUtil.updateNode("/demo", "auskat-3");
        Thread.sleep(Long.MAX_VALUE);
    }

    /**
     * 删除节点
     */
    @Test
    public void testDeleteNode() {
        zkUtil.deleteNode("/demo");
    }


}

