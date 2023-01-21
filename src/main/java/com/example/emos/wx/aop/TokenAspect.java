package com.example.emos.wx.aop;

import com.example.emos.wx.common.util.R;
import com.example.emos.wx.config.shiro.ThreadLocalToken;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @desc 拦截所有Web方法的返回值，在返回的R对象中添加更新后的令牌
 * @className: TokenAspect
 * @author: 康佳星
 * @date: 2023/1/21
 **/
@Aspect
@Component
public class TokenAspect {

    @Autowired
    private ThreadLocalToken threadLocalToken;

    @Pointcut("execution(public * com.example.emos.wx.controller.*.*(..)))")
    public void aspect() {}

    @Around("aspect()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        R r = (R) point.proceed(); // 方法执行结果
        String token = threadLocalToken.getToken();
        // 如果ThreadLocal中存在Token，说明是更新的Token
        if (token != null) {
            r.put("token", token);
            threadLocalToken.clear();
        }
        return r;
    }
}