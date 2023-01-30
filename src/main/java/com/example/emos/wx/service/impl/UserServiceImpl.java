package com.example.emos.wx.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emos.wx.db.dao.TbUserDao;
import com.example.emos.wx.exception.EmosException;
import com.example.emos.wx.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

/**
 * @desc 实现超级管理员与新用户注册
 * @className: UserServiceImpl
 * @author: 康佳星
 * @date: 2023/1/29
 **/
@Service
@Slf4j
@Scope("prototype")
public class UserServiceImpl implements UserService {
    @Value("${wx.app-id}")
    private String appId;
    @Value("${wx.app-secret}")
    private String appSecret;
    @Autowired
    private TbUserDao userDao;

    /**
     * @desc 获取微信小程序openId
     * @Author 康佳星
     * @Date 2023/1/29
     * @param code js_code
     */
    private String getOpenId(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        HashMap map = new HashMap();
        map.put("appid", appId);
        map.put("secret", appSecret);
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String response = HttpUtil.post(url, map);
        JSONObject json = JSONUtil.parseObj(response);
        String openid = json.getStr("openid");
        if (openid == null || openid.length() == 0) {
            throw new RuntimeException("临时登录凭证错误");
        }
        return openid;
    }

    @Override
    public int registerUser(String registerCode, String code, String nickname, String photo) {
        // 若邀请码为000000， 代表用户为超级管理员
        if (registerCode.equals("000000")) {
            // 查询超级管理员是否已绑定
            boolean bool = userDao.haveRootUser();
            if (!bool) {
                String openId = getOpenId(code);
                HashMap param = new HashMap();
                param.put("openid", openId);
                param.put("nickname", nickname);
                param.put("photo", photo);
                param.put("role", "[0]");
                param.put("status", 1);
                param.put("createTime", new Date());
                param.put("root", true);
                userDao.insert(param);
                int id = userDao.searchIdByOpenId(openId);
                return id;
            } else {
                throw new EmosException("超级管理员已绑定");
            }
        } else {
            return  0;
        }
    }

    /**
     * @desc 查询客户权限
     * @Author 康佳星
     * @Date 2023/1/29
     * @param userId 客户id
     */
    public Set<String> searchUserPermissions(int userId) {
        Set<String> permissions=userDao.searchUserPermissions(userId);
        return permissions;
    }
}
