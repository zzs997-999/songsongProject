package com.hmdp.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.hmdp.dto.UserDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.LOGIN_USER_KEY;
import static com.hmdp.utils.RedisConstants.LOGIN_USER_TTL;

public class RefreshTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取请求头中的token，字段为authorization
        String token=request.getHeader("authorization");
        if(StrUtil.isBlank(token)){
            //token为空，不用刷新，不拦截
            return true;
        }
        //基于token获取redis中的用户
        String key=LOGIN_USER_KEY+token;
        Map<Object,Object>userMap=stringRedisTemplate.opsForHash().entries(key);
        if(userMap.isEmpty()){
            //不用刷新，不拦截
            return true;
        }
        //将查询到的map数据转换为userDTO
        UserDTO userDTO=BeanUtil.fillBeanWithMap(userMap,new UserDTO(),false);
        //将用户信息保存到threadlocal
        UserHolder.saveUser(userDTO);
        //最重要一步，刷新token有效期
        stringRedisTemplate.expire(key,LOGIN_USER_TTL,TimeUnit.MINUTES);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户
        UserHolder.removeUser();
    }
}
