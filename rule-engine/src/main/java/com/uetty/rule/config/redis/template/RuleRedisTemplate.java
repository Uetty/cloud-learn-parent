package com.uetty.rule.config.redis.template;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

public class RuleRedisTemplate extends ReactiveStringRedisTemplate {

    public RuleRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        super(connectionFactory, RedisSerializationContext.string());
    }

    public RuleRedisTemplate(ReactiveRedisConnectionFactory connectionFactory, RedisSerializationContext<String, String> serializationContext) {
        super(connectionFactory, serializationContext);
    }

    public RuleRedisTemplate(ReactiveRedisConnectionFactory connectionFactory, RedisSerializationContext<String, String> serializationContext, boolean exposeConnection) {
        super(connectionFactory, serializationContext, exposeConnection);
    }
}
