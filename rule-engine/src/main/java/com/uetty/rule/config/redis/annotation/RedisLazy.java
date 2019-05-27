package com.uetty.rule.config.redis.annotation;

import java.lang.annotation.*;

/**
 * redis 懒加载
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLazy {

    /**
     * @return 使用的redisTemplate
     */
    String redisTemplate() default "redisTemplateRule";

    /**
     * @return redis存储key
     */
    String value();

}
