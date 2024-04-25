package com.lianxi.pg.feignClient;

import com.lianxi.core.domain.R;
import com.lianxi.pg.entity.Articles;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "lianxi-biz2", path = "/Articles")
public interface RemoteServiceClient {
    @GetMapping("/test/{id}")
    R<Articles> test(@PathVariable("id") Integer id);

    @GetMapping("/delete/{id}")
    R<Integer> delete(@PathVariable("id") Integer id);
}
