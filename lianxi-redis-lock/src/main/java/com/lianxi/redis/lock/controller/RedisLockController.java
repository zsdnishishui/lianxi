package com.lianxi.redis.lock.controller;


import com.lianxi.core.domain.R;
import com.lianxi.redis.service.RedisService;
import com.lianxi.redis.service.RedissonLock;
import com.lianxi.redis.service.configure.RedissonRedLockConfig;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonRedLock;
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
@Slf4j
public class RedisLockController {

    @Autowired
    private RedissonLock redissonLock;

    @Autowired
    private RedissonRedLockConfig redissonRedLockConfig;

    @Autowired
    private RedisService redisService;

    @GetMapping("/lock")
    public R lock() {
        //模拟多个10个客户端
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                if (redisService.getLock("demo", "demo", 10)) {
                    System.out.println(Thread.currentThread().getName() + ":  获取到锁");
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    redisService.releaseLock("demo", "demo");
                    System.out.println(Thread.currentThread().getName() + ":  释放锁");
                } else {
                    // 获取锁失败，重新获取
                    while (true) {
                        if (redisService.getLock("demo", "demo", 10)) {
                            System.out.println(Thread.currentThread().getName() + ":  获取到锁");
                            try {
                                TimeUnit.SECONDS.sleep(3);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            redisService.releaseLock("demo", "demo");
                            System.out.println(Thread.currentThread().getName() + ":  释放锁");
                            break;
                        }
                        try {
                            // 时间长了安全性更高，不然一把锁会被多个线程同时抢占
                            TimeUnit.MILLISECONDS.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }


            });
            thread.start();
        }
        return R.ok();
    }

    /**
     * 单节点锁
     *
     * @return
     */
    @GetMapping("/easyLock")
    public R easyLock() {
        //模拟多个10个客户端
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new LockRunnable());
            thread.start();
        }
        return R.ok();
    }

    @GetMapping("/redLock")
    public R redLock() {
        //模拟多个10个客户端
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new RedLockRunnable());
            thread.start();
        }
        return R.ok();
    }

    private class LockRunnable implements Runnable {
        @Override
        public void run() {
            redissonLock.addLock("demo");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            redissonLock.releaseLock("demo");
        }
    }

    private class RedLockRunnable implements Runnable {
        @Override
        public void run() {
            RedissonRedLock redissonRedLock = redissonRedLockConfig.initRedissonClient("demo");

            try {
                boolean lockResult = redissonRedLock.tryLock(100, 10, TimeUnit.SECONDS);

                if (lockResult) {
                    System.out.println(Thread.currentThread().getName() + ":  获取锁成功");
                    TimeUnit.SECONDS.sleep(3);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                redissonRedLock.unlock();
                System.out.println(Thread.currentThread().getName() + ":  释放锁");
            }
        }
    }
}