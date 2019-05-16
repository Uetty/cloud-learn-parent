package com.uetty.rule.config.redis.operations.impl;

import com.uetty.rule.config.redis.operations.ClassReactiveHashOperations;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ClassReactiveHashOperationsImpl<H, HK, HV> implements ClassReactiveHashOperations<H, HK, HV> {

    private final @NonNull ReactiveRedisTemplate<?, ?> template;
    private final @NonNull RedisSerializationContext<H, ?> serializationContext;

    @Override
    public Mono<Long> remove(H key, Object... hashKeys) {
        return null;
    }

    @Override
    public Mono<Boolean> hasKey(H key, Object hashKey) {
        return null;
    }

    @Override
    public Mono<HV> get(H key, Object hashKey) {
        return null;
    }

    @Override
    public Mono<List<HV>> multiGet(H key, Collection<HK> hashKeys) {
        return null;
    }

    @Override
    public Mono<Long> increment(H key, HK hashKey, long delta) {
        return null;
    }

    @Override
    public Mono<Double> increment(H key, HK hashKey, double delta) {
        return null;
    }

    @Override
    public Flux<HK> keys(H key) {
        return null;
    }

    @Override
    public Mono<Long> size(H key) {
        return null;
    }

    @Override
    public Mono<Boolean> putAll(H key, Map<? extends HK, ? extends HV> map) {
        return null;
    }

    @Override
    public Mono<Boolean> put(H key, HK hashKey, HV value) {
        return null;
    }

    @Override
    public Mono<Boolean> putIfAbsent(H key, HK hashKey, HV value) {
        return null;
    }

    @Override
    public Flux<HV> values(H key) {
        return null;
    }

    @Override
    public Flux<Map.Entry<HK, HV>> entries(H key) {
        return null;
    }

    @Override
    public Flux<Map.Entry<HK, HV>> scan(H key, ScanOptions options) {
        return null;
    }

    @Override
    public Mono<Boolean> delete(H key) {
        return null;
    }
}
