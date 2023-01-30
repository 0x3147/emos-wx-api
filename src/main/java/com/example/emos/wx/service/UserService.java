package com.example.emos.wx.service;

import java.util.Set;

/**
 * @desc 实现超级管理员与新用户注册
 * @className: UserService
 * @author: 康佳星
 * @date: 2023/1/29
 **/
public interface UserService {
    /**
     * @desc 注册用户
     * @Author 康佳星
     * @Date 2023/1/29
     */
    int registerUser(String registerCode,String code,String nickname,String photo);

    /**
     * @desc 查询客户权限
     * @Author 康佳星
     * @Date 2023/1/30
     */
    Set<String> searchUserPermissions(int userId);
}
