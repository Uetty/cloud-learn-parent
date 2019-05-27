package com.uetty.rule.config.redis;

import com.uetty.rule.entity.User;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

/**
 * redis 代理初始化
 */
@Aspect
@Component
public class RedisProxyInit {


    public void initRedisProxy() {
        Proxy.newProxyInstance(User.class.getClassLoader(), User.class.getInterfaces(), ((proxy, method, args) -> {
            //GET 方法
            if (method.getName().startsWith("get")) {
                //判断这个属性是否为空
            }
            return method.invoke(proxy, args);
        }));
    }

    /**
     * @return redis懒加载AOP切面
     */
    @Pointcut("@annotation(com.uetty.rule.config.redis.annotation.RedisLazy)")
    public void redisLazy() {

    }

    /**
     * 前置通知
     * @param joinPoint 连接点
     */
    @Before("redisLazy()")
    public void beforeAdvice(JoinPoint joinPoint) {
        joinPoint.getTarget();
    }
}
