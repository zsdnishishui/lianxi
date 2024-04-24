package com.lianxi.pg.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 消息接口
 * </p>
 *
 * @author 福隆苑居士，公众号：【Java分享客栈】
 * @since 2022-04-02 12:00
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class TestController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("test")
    public void test() {
        Long count = jdbcTemplate.queryForObject("select count(*) from articles", Long.class);
        log.info("记录总数：{}", count);
    }
}
