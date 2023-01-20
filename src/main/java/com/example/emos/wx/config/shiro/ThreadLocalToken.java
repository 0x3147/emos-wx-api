package com.example.emos.wx.config.shiro;

import org.springframework.stereotype.Component;

/**
 * @desc ThreadLocalToken
 * @className: ThreadLocalToken
 * @author: 康佳星
 * @date:  2023/1/20
 **/

@Component
public class ThreadLocalToken {
    private ThreadLocal<String> local = new ThreadLocal<>();

    public void setToken(String token) {
        this.local.set(token);
    }

    public String getToken() {
        return local.get();
    }

    public void clear() {
        local.remove();
    }
}
