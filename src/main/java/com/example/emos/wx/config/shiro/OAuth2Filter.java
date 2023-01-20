package com.example.emos.wx.config.shiro;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.emos.wx.exception.EmosException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @desc 请求拦截
 * @className: OAuth2Filter
 * @author: 康佳星
 * @date:  2023/1/20
 **/

@Component
@Scope("prototype")
public class OAuth2Filter extends AuthenticatingFilter {
    @Autowired
    private ThreadLocalToken threadLocalToken;

    @Value("${emos.jwt.cache-expire}")
    private int cacheExpire;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @desc 拦截请求之后，用于将令牌字符串封装为令牌对象
     * @Author 康佳星
     * @Date 2023/1/19
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        // 获取请求token
        String token = getRequestToken((HttpServletRequest) request);
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return new OAuth2Token(token);
    }

    /**
     * @desc 拦截请求，判断请求是否需要被shiro处理
     * @Author 康佳星
     * @Date 2023/1/19
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest req = (HttpServletRequest) request;
        // Ajax提交application/json数据时，会先发出Options请求
        // 这里需要放行Options请求，不需要shiro处理
        if (req.getMethod().equals(RequestMethod.OPTIONS.name())) {
            return true;
        }
        // 除Options请求之外，所有请求都要被shiro处理
        return false;
    }

    /**
     * @desc 该方法用于处理所有应该被shiro处理的请求
     * @Author 康佳星
     * @Date 2023/1/19
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        // 允许跨域
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));

        threadLocalToken.clear();
        // 获取请求token，如果token不存在，直接返回401
        String token = getRequestToken(req);
        if (StringUtils.isBlank(token)) {
            resp.setStatus(HttpStatus.SC_UNAUTHORIZED);
            resp.getWriter().print("无效的令牌");
            return false;
        }

        try {
            jwtUtil.verifierToken(token); // 检查令牌是否过期
        } catch (TokenExpiredException e) {
            // 客户端令牌过期，查询redis中是否存在令牌，如果存在令牌就重新生成一个令牌给客户端
            if (redisTemplate.hasKey(token)) {
                redisTemplate.delete(token); // 删除令牌
                int userId = 0;
                try {
                    userId = jwtUtil.getUserId(token);
                } catch (EmosException ex) {
                    throw new RuntimeException(ex);
                }
                token = jwtUtil.createToken(userId);
                redisTemplate.opsForValue().set(token,userId+"", cacheExpire, TimeUnit.DAYS);
                threadLocalToken.setToken(token);
            }
        } catch (JWTDecodeException e) {
            resp.setStatus(HttpStatus.SC_UNAUTHORIZED);
            resp.getWriter().print("无效的令牌");
            return false;
        }
        boolean bool=executeLogin(request,response);
        return bool;
    }

    /**
     * @desc 登录失败时的操作
     * @Author 康佳星
     * @Date 2023/1/20
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletRequest req= (HttpServletRequest) request;
        HttpServletResponse resp= (HttpServletResponse) response;
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        resp.setStatus(HttpStatus.SC_UNAUTHORIZED);
        try{
            resp.getWriter().print(e.getMessage());
        } catch (Exception exception){

        }
        return false;
    }

    /**
     * @desc 获取请求头里的token
     * @Author 康佳星
     * @Date 2023/1/19
     */
    private String getRequestToken(HttpServletRequest httpRequest) {
        // 从header中获取token
        String token = httpRequest.getHeader("token");

        // 如果header不存在token，则从参数中获取token
        if (StringUtils.isBlank(token)) {
            token = httpRequest.getParameter("token");
        }
        return token;
    }
}
