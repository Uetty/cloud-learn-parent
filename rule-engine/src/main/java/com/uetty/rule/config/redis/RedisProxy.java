package com.uetty.rule.config.redis;

import com.uetty.rule.entity.User;

import java.lang.reflect.Proxy;

/**
 * redis 代理初始化
 */
public class RedisProxy<T> {

    private T t;

    public static <T> T create(T t) {
        RedisProxy<T> redisProxy = new RedisProxy<>();
        redisProxy.t = t;
        redisProxy.initRedisProxy();
        return redisProxy.t;
    }

    public void initRedisProxy() {
        Proxy.newProxyInstance(t.getClass().getClassLoader(), t.getClass().getInterfaces(), ((proxy, method, args) -> {
            //GET 方法
            if (method.getName().startsWith("get")) {
                //判断这个属性是否为空
                if (method.invoke(t, args) == null) {
                    //设置属性值
                    proxy.getClass().getMethod(method.getName().replace("get", "set"), Object.class)
                            .invoke(t, "1");//method.invoke(t, args)
                }
            }
            return method.invoke(t, args);
        }));
    }

    public static void main(String[] args) {
        User user = new User();
        user = RedisProxy.create(user);
        String userName = user.getUserName();
        System.out.println(userName);
    }
}
