package com.example.emos.wx.controller;

import com.example.emos.wx.common.util.R;
import com.example.emos.wx.config.shiro.JwtUtil;
import com.example.emos.wx.controller.form.RegisterForm;
import com.example.emos.wx.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @desc 处理提交的请求
 * @className: UserController
 * @author: 康佳星
 * @date: 2023/1/30
 **/
@RestController
@RequestMapping("/user")
@Api("用户模块web接口")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${emos.jwt.cache-expire}")
    private int cacheExpire;

    /**
     * @desc 注册功能
     * @Author 康佳星
     * @Date 2023/1/30
     * @param form 表单类
     */
    @PostMapping("/register")
    @ApiOperation("注册用户")
    public R register(@Valid @RequestBody RegisterForm form) {
        int id = userService.registerUser(form.getRegisterCode(), form.getCode(), form.getNickName(), form.getPhoto());
        String token = jwtUtil.createToken(id);
        Set<String> permsSet = userService.searchUserPermissions(id);
        saveCacheToken(token, id);
        return R.ok("注册成功").put("token", token).put("permissions", permsSet);
    }

    /**
     * @desc 保存token
     * @Author 康佳星
     * @Date 2023/1/30
     * @param token 用户注册/登录时得到的token
     * @param userId 用户id
     */
    private void saveCacheToken(String token, int userId) {
        redisTemplate.opsForValue().set(token, userId + "", cacheExpire, TimeUnit.DAYS);
    }
}
