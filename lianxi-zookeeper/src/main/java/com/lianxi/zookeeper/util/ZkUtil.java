package com.lianxi.zookeeper.util;

import com.lianxi.zookeeper.config.ZookeeperConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 类文件: ZkUtil
 * <p>
 * <p>
 * 类描述：
 * <p>
 * 作     者： AusKa_T
 * <p>
 * 日     期： 2021/3/24 0024
 * <p>
 * 时     间： 9:35
 * <p>
 */
@Component
@Slf4j
public class ZkUtil {

    @Autowired
    private ZooKeeper zkClient;

    @Autowired
    private ZookeeperConfig ZooKeeper;


    /**
     * 创建持久化节点
     * -- 客户端断开连接后，节点数据持久化在磁盘上，不会被删除。
     *
     * @param path 路径
     * @param data 数据
     */
    public boolean createPerNode(String path, String data) {
        try {
            // 参数1：要创建的节点的路径； 参数2：节点数据 ； 参数3：节点权限 ；参数4：节点的类型
            zkClient.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            return true;
        } catch (Exception e) {
            log.error("创建持久化节点异常，路径: {}, 数据: {}, 异常: {}", path, data, e);
            return false;
        }
    }

    /**
     * 创建临时节点
     * -- 客户端断开连接后，节点将被删除。
     *
     * @param path 路径
     * @param data 数据
     */
    public boolean createTmpNode(String path, String data) {
        try {
            // 参数1：要创建的节点的路径； 参数2：节点数据 ； 参数3：节点权限 ；参数4：节点的类型
            zkClient.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            return true;
        } catch (Exception e) {
            log.error("创建临时节点异常，路径: {}, 数据: {}, 异常: {}", path, data, e);
            return false;
        }
    }

    /**
     * 创建自定义节点
     *
     * @param path       路径
     * @param data       数据
     * @param acl        节点权限
     * @param createMode 节点类型
     */
    public boolean createNode(String path, String data, List<ACL> acl, CreateMode createMode) {
        try {
            // 参数1：要创建的节点的路径； 参数2：节点数据 ； 参数3：节点权限 ；参数4：节点的类型
            zkClient.create(path, data.getBytes(), acl, createMode);
            return true;
        } catch (Exception e) {
            log.error("创建节点异常，路径: {}, 数据: {}, 异常: {}", path, data, e);
            return false;
        }
    }


    /**
     * 修改节点
     *
     * @param path 路径
     * @param data 数据
     */
    public boolean updateNode(String path, String data) {
        try {
            // zk的数据版本是从0开始计数的。如果客户端传入的是-1，则表示zk服务器需要基于最新的数据进行更新。如果对zk的数据节点的更新操作没有原子性要求则可以使用-1.
            // version参数指定要更新的数据的版本, 如果version和真实的版本不同, 更新操作将失败. 指定version为-1则忽略版本检查
            zkClient.setData(path, data.getBytes(), -1);
            return true;
        } catch (Exception e) {
            log.error("修改节点异常，路径: {}, 数据: {}, 异常: {}", path, data, e);
            return false;
        }
    }

    /**
     * 删除节点
     *
     * @param path 路径
     */
    public boolean deleteNode(String path) {
        try {
            // version参数指定要更新的数据的版本, 如果version和真实的版本不同, 更新操作将失败. 指定version为-1则忽略版本检查
            zkClient.delete(path, -1);
            return true;
        } catch (Exception e) {
            log.error("删除节点异常，路径: {}, 异常: {}", path, e);
            return false;
        }
    }

    /**
     * 判断指定节点是否存在
     *
     * @param path      路径
     * @param needWatch 指定是否复用zookeeper中默认的Watcher
     * @return 结果
     */
    public Stat exists(String path, boolean needWatch) {
        try {
            return zkClient.exists(path, needWatch);
        } catch (Exception e) {
            log.error("判断指定节点是否存在异常，路径: {}, 异常: {}", path, e);
            return null;
        }
    }

    /**
     * 检测结点是否存在 并设置监听事件
     * 三种监听类型： 创建，删除，更新
     *
     * @param path    路径
     * @param watcher 传入指定的监听类
     */
    public Stat exists(String path, Watcher watcher) {
        try {
            return zkClient.exists(path, watcher);
        } catch (Exception e) {
            log.error("判断指定节点是否存在异常，路径: {}, 异常: {}", path, e);
            return null;
        }
    }


    /**
     * 获取当前节点的子节点(不包含孙子节点)
     *
     * @param path 父节点path
     */
    public List<String> getChildren(String path) throws KeeperException, InterruptedException {
        List<String> list = zkClient.getChildren(path, false);
        return list;
    }

    /**
     * 获取指定节点的值
     *
     * @param path 路径
     */
    public String getData(String path, Watcher watcher) {
        try {
            Stat stat = new Stat();
            byte[] bytes = zkClient.getData(path, watcher, stat);
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    /**
//     * 注册监听
//     * @param watcher 监听类
//     */
//    public void registerWatch(Watcher watcher) throws IOException {
//        ZooKeeper zooKeeper = new ZooKeeper(ZooKeeper.getConnectString(), ZooKeeper.getSessionTimeout(), watcher);
//    }


}

