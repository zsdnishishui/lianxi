package com.lianxi.redis.lock.config;

import org.redisson.Redisson;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 多节点的锁
 */
@Configuration
public class RedissonRedLockConfig {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;

    public RedissonRedLock initRedissonClient(String lockKey) {

        Config config1 = new Config();
        config1.useSingleServer().setAddress("redis://" + host + ":" + port).setDatabase(0);
        RedissonClient redissonClient1 = Redisson.create(config1);

        Config config2 = new Config();
        config2.useSingleServer().setAddress("redis://" + host + ":" + port).setDatabase(1);
        RedissonClient redissonClient2 = Redisson.create(config2);

        Config config3 = new Config();
        config3.useSingleServer().setAddress("redis://" + host + ":" + port).setDatabase(2);
        RedissonClient redissonClient3 = Redisson.create(config3);

        Config config4 = new Config();
        config4.useSingleServer().setAddress("redis://" + host + ":" + port).setDatabase(3);
        RedissonClient redissonClient4 = Redisson.create(config4);

        Config config5 = new Config();
        config5.useSingleServer().setAddress("redis://" + host + ":" + port).setDatabase(4);
        RedissonClient redissonClient5 = Redisson.create(config5);

        RLock rLock1 = redissonClient1.getLock(lockKey);
        RLock rLock2 = redissonClient2.getLock(lockKey);
        RLock rLock3 = redissonClient3.getLock(lockKey);
        RLock rLock4 = redissonClient4.getLock(lockKey);
        RLock rLock5 = redissonClient5.getLock(lockKey);

        RedissonRedLock redissonRedLock = new RedissonRedLock(rLock1, rLock2, rLock3, rLock4, rLock5);

        return redissonRedLock;
    }
}
