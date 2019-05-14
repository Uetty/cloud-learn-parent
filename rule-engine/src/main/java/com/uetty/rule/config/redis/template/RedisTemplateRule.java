package com.uetty.rule.config.redis.template;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Objects;

public class RedisTemplateRule<K,V> extends ReactiveRedisTemplate<K,V> {

    public RedisTemplateRule(ReactiveRedisConnectionFactory connectionFactory) {
        super(connectionFactory, Objects.requireNonNull(redisSerializationContext()));
    }

    public RedisTemplateRule(ReactiveRedisConnectionFactory connectionFactory, RedisSerializationContext<String, String> serializationContext) {
        super(connectionFactory, Objects.requireNonNull(redisSerializationContext()));
    }

    public RedisTemplateRule(ReactiveRedisConnectionFactory connectionFactory, RedisSerializationContext<String, String> serializationContext, boolean exposeConnection) {
        super(connectionFactory, Objects.requireNonNull(redisSerializationContext()), exposeConnection);
    }

    private static <K, V> RedisSerializationContext<K, V> redisSerializationContext(){
        RedisSerializationContext<K,V> redisSerializationContext = (RedisSerializationContext<K, V>) RedisSerializationContext.fromSerializer(RedisSerializer.json());
        return redisSerializationContext;
    }
}
