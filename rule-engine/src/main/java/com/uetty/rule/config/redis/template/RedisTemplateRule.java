package com.uetty.rule.config.redis.template;

import com.uetty.rule.config.redis.JacksonRedisSerializer;
import com.uetty.rule.config.redis.operations.ClassReactiveHashOperations;
import com.uetty.rule.config.redis.operations.impl.ClassReactiveHashOperationsImpl;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

public class RedisTemplateRule<K, V> extends ReactiveRedisTemplate<K, V> {

    public RedisTemplateRule(ReactiveRedisConnectionFactory connectionFactory) {
        super(connectionFactory, redisSerializationContext());
    }

    public RedisTemplateRule(ReactiveRedisConnectionFactory connectionFactory, RedisSerializationContext<String, String> serializationContext) {
        super(connectionFactory, redisSerializationContext());
    }

    public RedisTemplateRule(ReactiveRedisConnectionFactory connectionFactory, RedisSerializationContext<String, String> serializationContext, boolean exposeConnection) {
        super(connectionFactory, redisSerializationContext(), exposeConnection);
    }

    @SuppressWarnings("unchecked")
    private static <K, V> RedisSerializationContext<K, V> redisSerializationContext() {
        return (RedisSerializationContext<K, V>) RedisSerializationContext.fromSerializer(new JacksonRedisSerializer<>());
    }

    @Override
    public <HK, HV> ClassReactiveHashOperations<K, HK, HV> opsForHash() {
        return opsForHash(redisSerializationContext());
    }

    @Override
    public <K1, HK, HV> ClassReactiveHashOperations<K1, HK, HV> opsForHash(RedisSerializationContext<K1, ?> serializationContext) {
        return new ClassReactiveHashOperationsImpl<>(this, serializationContext);
    }
}
