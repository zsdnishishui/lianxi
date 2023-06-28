package com.lianxi.IM.enity;

import java.security.Principal;

/**
 * @description: stomp连接标识实体类
 * @data: 2022/12/14
 */
public class User implements Principal {

    /**
     * 用户名-唯一标识
     */
    private String userName;

    /**
     * 必须实现，连接唯一标识
     *
     * @return
     */
    @Override
    public String getName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
