package com.uetty.rule.config.redis;

import com.uetty.rule.entity.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Proxy;

/**
 * redis 代理初始化
 */
@Component
public class RedisProxyInit {


    @PostConstruct
    public static void initRedisProxy() {
        Object get = Proxy.newProxyInstance(User.class.getClassLoader(), User.class.getInterfaces(), ((proxy, method, args) -> {
            //GET 方法
            if (method.getName().startsWith("get")) {
                //判断这个属性是否为空
            }
            return method.invoke(proxy, args);
        }));
        System.out.println(11);
    }

    public static void main(String[] args) {
        initRedisProxy();
        User user = new User();
        Integer userId = user.getUserId();
        System.out.println(1);
    }
}
