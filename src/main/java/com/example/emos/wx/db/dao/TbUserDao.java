package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Mapper
public interface TbUserDao {

    /**
     * @desc 查询是否存在超级管理员
     * @Author 康佳星
     * @Date 2023/1/27
     */
    boolean haveRootUser();

    /**
     * @desc 保存用户记录
     * @Author 康佳星
     * @Date 2023/1/27
     * @param param 用户记录
     */
    int insert(HashMap param);

    /**
     * @desc 根据OpenId查询用户ID
     * @Author 康佳星
     * @Date 2023/1/27
     * @param openId 小程序openId
     */
    Integer searchIdByOpenId(String openId);
}
