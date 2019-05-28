package com.uetty.rule.config.redis;

import com.uetty.rule.entity.User;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

/**
 * redis 代理初始化
 */
@Component
public class RedisProxyInit<T> {

    @SuppressWarnings("unchecked")
    public void initRedisProxy(T t) {
        Proxy.newProxyInstance(t.getClass().getClassLoader(), t.getClass().getInterfaces(), ((proxy, method, args) -> {
            //GET 方法
            if (method.getName().startsWith("get")) {
                //判断这个属性是否为空
                if (method.invoke(proxy, args) == null) {
                    //设置属性值
                    proxy.getClass().getMethod(method.getName().replace("get", "set"), Object.class)
                            .invoke(proxy, method.invoke(proxy, args));
                }
            }
            return method.invoke(proxy, args);
        }));

    }

    public static void main(String[] args) {
        RedisProxyInit<User> userRedisProxyInit = new RedisProxyInit<>();
        User user = new User();
        userRedisProxyInit.initRedisProxy(user);
        String userName = user.getUserName();
        System.out.println(userName);
    }
}
