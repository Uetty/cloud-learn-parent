package com.uetty.rule.config.redis.template;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

public class RedisTemplateRule extends ReactiveStringRedisTemplate {

    public RedisTemplateRule(ReactiveRedisConnectionFactory connectionFactory) {
        super(connectionFactory, RedisSerializationContext.string());
    }

    public RedisTemplateRule(ReactiveRedisConnectionFactory connectionFactory, RedisSerializationContext<String, String> serializationContext) {
        super(connectionFactory, serializationContext);
    }

    public RedisTemplateRule(ReactiveRedisConnectionFactory connectionFactory, RedisSerializationContext<String, String> serializationContext, boolean exposeConnection) {
        super(connectionFactory, serializationContext, exposeConnection);
    }
}
