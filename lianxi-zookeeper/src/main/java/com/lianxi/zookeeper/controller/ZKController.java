package com.lianxi.zookeeper.controller;


import com.lianxi.core.domain.R;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 文件请求处理
 *
 * @author ruoyi
 */
@RestController
public class ZKController {
    @Autowired
    private CuratorFramework curatorClient;

    @GetMapping("/test")
    public R test() {
        InterProcessMutex lock = new InterProcessMutex(curatorClient, "/zsd");
        try {
            //等待60s,请求锁
            if (lock.acquire(60, TimeUnit.SECONDS)) {
                System.out.println(Thread.currentThread().getName() + " get & hold the lock");
                //占用锁5s
                Thread.sleep(5000);
                System.out.println(Thread.currentThread().getName() + " release the lock");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                lock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return R.ok();
    }

}