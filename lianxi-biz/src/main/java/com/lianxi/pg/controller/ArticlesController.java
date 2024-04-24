package com.lianxi.pg.controller;

import com.lianxi.core.domain.R;
import com.lianxi.pg.entity.Articles;
import com.lianxi.pg.service.ArticlesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 消息接口
 * </p>
 *
 * @author 福隆苑居士，公众号：【Java分享客栈】
 * @since 2022-04-02 12:00
 */
@RestController
@RequestMapping("/Articles")
@Slf4j
public class ArticlesController {
    @Autowired
    private ArticlesService articlesService;

    @GetMapping("/test/{id}")
    public R<Articles> test(@PathVariable("id") Integer id) {
        return R.ok(articlesService.getById(id));
    }

    @GetMapping("/delete/{id}")
    public R<Integer> delete(@PathVariable("id") Integer id) {
        return R.ok(articlesService.delete(id));
    }

    @PostMapping("/update")
    public R<Integer> update(@RequestBody Articles articles) {
        return R.ok(articlesService.update(articles));
    }

    @PostMapping("/add")
    public R<Integer> add(@RequestBody Articles articles) {
        return R.ok(articlesService.add(articles));
    }
}
