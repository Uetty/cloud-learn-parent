package com.uetty.rule.config.redis.operations.impl;

import com.google.common.collect.Lists;
import com.uetty.rule.config.redis.operations.ReactiveLuaOperations;
import com.uetty.rule.config.redis.script.ScriptConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class ReactiveLuaOperationsImpl<K, V> implements ReactiveLuaOperations<K, V> {

    private final @NonNull ReactiveRedisTemplate<K, V> template;
    private final @NonNull RedisSerializationContext<?, ?> serializationContext;

    @Override
    public Mono<List<V>> getHashFromSortedSet(K sortedSetKey, K hashKey, long start, long end) {
        Assert.notNull(sortedSetKey, "sortedSetKey must not be null!");
        Assert.notNull(hashKey, "hashKey must not be null!");
        List<K> keys = Lists.newArrayList();
        keys.add(sortedSetKey);
        keys.add(hashKey);
        return template.execute(ScriptConfig.<List<V>>getScript(ScriptConfig.ScriptType.GET_HASH_FROM_ZSET), keys,
                Lists.newArrayList(start, end)).last();


    }
}
