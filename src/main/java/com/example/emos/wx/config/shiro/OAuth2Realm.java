package com.example.emos.wx.config.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @desc 这个实现类中定义认证和授权的方法
 * @className: OAuth2Realm
 * @author: 康佳星
 * @date:  2023/1/20
 **/

@Component
public class OAuth2Realm extends AuthorizingRealm {

    @Autowired
    private  JwtUtil jwtUtil;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    /**
     * @desc 授权(验证权限时调用)
     * @Author 康佳星
     * @Date 2023/1/19
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // TODO 查询用户的权限列表
        // TODO 把权限列表添加到info列表中
        return info;
    }

    /**
     * @desc 认证(登录时调用)
     * @Author 康佳星
     * @Date 2023/1/19
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // TODO 从令牌中获取userId 然后检测该账户是否被冻结
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo();
        // TODO 向info中添加用户信息、token字符串
        return info;
    }
}
