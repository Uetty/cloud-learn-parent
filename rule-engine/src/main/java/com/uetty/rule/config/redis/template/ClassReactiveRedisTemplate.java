package com.uetty.rule.config.redis.template;

import com.uetty.rule.config.redis.JacksonRedisSerializer;
import com.uetty.rule.config.redis.operations.ReactiveClassOperations;
import com.uetty.rule.config.redis.operations.ReactiveLuaOperations;
import com.uetty.rule.config.redis.operations.impl.ReactiveClassOperationsImpl;
import com.uetty.rule.config.redis.operations.impl.ReactiveLuaOperationsImpl;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

public class ClassReactiveRedisTemplate<K, V> extends ReactiveRedisTemplate<K, V> {

    public ClassReactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        super(connectionFactory, redisSerializationContext());
    }

    public ClassReactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory, RedisSerializationContext<String, String> serializationContext) {
        super(connectionFactory, redisSerializationContext());
    }

    public ClassReactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory, RedisSerializationContext<String, String> serializationContext, boolean exposeConnection) {
        super(connectionFactory, redisSerializationContext(), exposeConnection);
    }

    @SuppressWarnings({"unchecked", "NullableProblems"})
    private static <K, V> RedisSerializationContext<K, V> redisSerializationContext() {
        RedisSerializationContext<String, String> string = RedisSerializationContext.string();
        RedisSerializationContext<K, V> clazz = (RedisSerializationContext<K, V>) RedisSerializationContext.fromSerializer(new JacksonRedisSerializer<>());
        return new RedisSerializationContext<K, V>() {

            @Override
            public SerializationPair getKeySerializationPair() {
                return string.getKeySerializationPair();
            }

            @Override
            public SerializationPair getValueSerializationPair() {
                return clazz.getValueSerializationPair();
            }

            @Override
            public SerializationPair<String> getStringSerializationPair() {
                return clazz.getStringSerializationPair();
            }

            @Override
            public SerializationPair<V> getHashValueSerializationPair() {
                return clazz.getHashValueSerializationPair();
            }

            @Override
            public SerializationPair<K> getHashKeySerializationPair() {
                return string.getHashKeySerializationPair();
            }
        };
    }

    public <HK, HV> ReactiveClassOperations<K, HK, HV> opsForClass() {
        return opsForClass(redisSerializationContext());
    }

    public <K1, HK, HV> ReactiveClassOperations<K1, HK, HV> opsForClass(RedisSerializationContext<K1, ?> serializationContext) {
        return new ReactiveClassOperationsImpl<>(this, serializationContext);
    }

    public ReactiveLuaOperations opsForLua() {
        return opsForLua(redisSerializationContext());
    }

    public ReactiveLuaOperations opsForLua(RedisSerializationContext<?, ?> serializationContext) {
        return new ReactiveLuaOperationsImpl(this, serializationContext);
    }
}

