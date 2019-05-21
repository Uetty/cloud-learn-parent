package com.uetty.rule.config.redis.template;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;

public class RedisTemplateRule<K, V> extends ClassReactiveRedisTemplate<K, V> {

    public RedisTemplateRule(ReactiveRedisConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    public RedisTemplateRule(ReactiveRedisConnectionFactory connectionFactory, RedisSerializationContext<String, String> serializationContext) {
        super(connectionFactory, serializationContext);
    }

    public RedisTemplateRule(ReactiveRedisConnectionFactory connectionFactory, RedisSerializationContext<String, String> serializationContext, boolean exposeConnection) {
        super(connectionFactory, serializationContext, exposeConnection);
    }
}
