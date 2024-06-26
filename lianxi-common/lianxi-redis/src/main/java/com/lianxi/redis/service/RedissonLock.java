package com.lianxi.redis.service;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonLock {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 加锁
     *
     * @param lockKey
     * @return
     */
    public boolean addLock(String lockKey) {
        try {
            if (redissonClient == null) {
                System.out.println("redisson client is null");
                return false;
            }

            RLock lock = redissonClient.getLock(lockKey);
            //设置锁超时时间为10秒，到期自动释放
            lock.lock(10, TimeUnit.SECONDS);
            System.out.println(Thread.currentThread().getName() + ":  获取到锁");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean releaseLock(String lockKey) {

        try {
            if (redissonClient == null) {
                System.out.println("redisson client is null");
                return false;
            }

            RLock lock = redissonClient.getLock(lockKey);
            lock.unlock(); // 释放锁
            System.out.println(Thread.currentThread().getName() + ":  释放锁");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
