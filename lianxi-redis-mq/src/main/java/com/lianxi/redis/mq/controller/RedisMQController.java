package com.lianxi.redis.mq.controller;


import com.lianxi.core.domain.R;
import com.lianxi.redis.service.RedisStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 文件请求处理
 *
 * @author ruoyi
 */
@RestController
@Slf4j
public class RedisMQController {

    @Resource
    public RedisTemplate redisTemplate;

    @Resource
    public RedisStream redisStream;

    /**
     * 入队
     *
     * @return
     */
    @GetMapping("/join")
    public R join(String join) {
        return R.ok(redisTemplate.opsForList().leftPush("list", join));
    }

    /**
     * 出队
     *
     * @return
     */
    @GetMapping("/rightPop")
    public R rightPop() {
        //阻塞式
        //rightPop(K key, long timeout, TimeUnit unit) 移除并获取列表中最右边的元素(如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止)
        //非阻塞式
        return R.ok(redisTemplate.opsForList().rightPop("list"));
    }

    /**
     * 发布pub
     *
     * @return
     */
    @GetMapping("/pub")
    public R pub(String message) {
        redisTemplate.convertAndSend("test1", message);
        return R.ok();
    }


    /**
     * 发布pub
     *
     * @return
     */
    @GetMapping("/stream")
    public R stream(String message) {
        log.info(message);
        redisStream.add("stream1", "stream", message);
        return R.ok();
    }
}