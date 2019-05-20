package com.uetty.rule.config.redis.operations.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.uetty.rule.config.redis.annotation.RedisPrimaryKey;
import com.uetty.rule.config.redis.operations.ClassReactiveHashOperations;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.data.redis.connection.ReactiveHashCommands;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@SuppressWarnings({"NullableProblems", "unchecked"})
public class ClassReactiveHashOperationsImpl<H, HK, HV> implements ClassReactiveHashOperations<H, HK, HV> {

    private final @NonNull ReactiveRedisTemplate<?, ?> template;
    private final @NonNull RedisSerializationContext<H, ?> serializationContext;

    private final @NonNull RedisSerializationContext<Object, ?> serializationString = RedisSerializationContext.java();

    private final static String CLASS = "@class";

    @Override
    public Mono<Long> remove(H key, Object... hashKeys) {
        Assert.notNull(key, "Key must not be null!");
        Assert.notNull(hashKeys, "Hash keys must not be null!");
        Assert.notEmpty(hashKeys, "Hash keys must not be empty!");
        Assert.noNullElements(hashKeys, "Hash keys must not contain null elements!");
        return createMono(connection -> Flux.fromArray(hashKeys)
                .map(o -> (HK) o).map(this::rawHashKey)
                .collectList()
                .flatMap(hks -> connection.hDel(rawKey(key), hks)));
    }

    @Override
    public Mono<Boolean> hasKey(H key, Object hashKey) {
        Assert.notNull(key, "Key must not be null!");
        Assert.notNull(hashKey, "Hash key must not be null!");
        return createMono(connection -> connection.hExists(rawKey(key), rawHashKey((HK) hashKey)));
    }

    @Override
    public Mono<HV> get(H key, Object hashKey) {
        Assert.notNull(key, "Key must not be null!");
        Assert.notNull(hashKey, "Hash key must not be null!");
        return createMono(connection -> connection.hGet(rawKey(key), rawHashKey((HK) hashKey)).map(this::readHashValue));
    }

    @Override
    public Mono<List<HV>> multiGet(H key, Collection<HK> hashKeys) {
        Assert.notNull(key, "Key must not be null!");
        Assert.notNull(hashKeys, "Hash keys must not be null!");
        Assert.notEmpty(hashKeys, "Hash keys must not be empty!");
        return createMono(connection -> Flux.fromIterable(hashKeys)
                .map(this::rawHashKey)
                .collectList()
                .flatMap(hks -> connection.hMGet(rawKey(key), hks)).map(this::deserializeHashValues));
    }

    @Override
    public Mono<Long> increment(H key, HK hashKey, long delta) {
        Assert.notNull(key, "Key must not be null!");
        Assert.notNull(hashKey, "Hash key must not be null!");
        return template.createMono(connection -> connection
                .numberCommands()
                .hIncrBy(rawKey(key), rawHashKey(hashKey), delta));
    }

    @Override
    public Mono<Double> increment(H key, HK hashKey, double delta) {
        Assert.notNull(key, "Key must not be null!");
        Assert.notNull(hashKey, "Hash key must not be null!");
        return template.createMono(connection -> connection
                .numberCommands()
                .hIncrBy(rawKey(key), rawHashKey(hashKey), delta));
    }

    @Override
    public Flux<HK> keys(H key) {
        Assert.notNull(key, "Key must not be null!");
        return createFlux(connection -> connection.hKeys(rawKey(key))
                .map(this::readHashKey));
    }

    @Override
    public Mono<Long> size(H key) {
        Assert.notNull(key, "Key must not be null!");
        return createMono(connection -> connection.hLen(rawKey(key)));
    }

    @Override
    public Mono<Boolean> putAll(H key, Map<? extends HK, ? extends HV> map) {
        Assert.notNull(key, "Key must not be null!");
        Assert.notNull(map, "Map must not be null!");
        return createMono(connection -> Flux.fromIterable(() -> map.entrySet().iterator())
                .collectMap(entry -> rawHashKey(entry.getKey()), entry -> rawHashValue(entry.getValue()))
                .flatMap(serialized -> connection.hMSet(rawKey(key), serialized)));
    }

    @Override
    public Mono<Boolean> put(H key, HK hashKey, HV value) {
        Assert.notNull(key, "Key must not be null!");
        Assert.notNull(hashKey, "Hash key must not be null!");
        Assert.notNull(value, "Hash value must not be null!");
        return createMono(connection -> connection.hSet(rawKey(key), rawHashKey(hashKey), rawHashValue(value)));
    }

    @Override
    public Mono<Boolean> putIfAbsent(H key, HK hashKey, HV value) {
        Assert.notNull(key, "Key must not be null!");
        Assert.notNull(hashKey, "Hash key must not be null!");
        Assert.notNull(value, "Hash value must not be null!");
        return createMono(connection -> connection.hSetNX(rawKey(key), rawHashKey(hashKey), rawHashValue(value)));
    }

    @Override
    public Flux<HV> values(H key) {
        Assert.notNull(key, "Key must not be null!");
        return createFlux(connection -> connection.hVals(rawKey(key))
                .map(this::readHashValue));
    }

    @Override
    public Flux<Map.Entry<HK, HV>> entries(H key) {
        Assert.notNull(key, "Key must not be null!");
        return createFlux(connection -> connection.hGetAll(rawKey(key)) //
                .map(this::deserializeHashEntry));
    }

    @Override
    public Flux<Map.Entry<HK, HV>> scan(H key, ScanOptions options) {
        Assert.notNull(key, "Key must not be null!");
        Assert.notNull(key, "ScanOptions must not be null!");
        return createFlux(connection -> connection.hScan(rawKey(key), options) //
                .map(this::deserializeHashEntry));
    }

    @Override
    public Mono<Boolean> delete(H key) {
        Assert.notNull(key, "Key must not be null!");
        return template.createMono(connection -> connection.keyCommands().del(rawKey(key))).map(l -> l != 0);
    }

    /**
     * @return hashKey 序列化
     */
    private ByteBuffer rawHashKey(Object key) {
        return serializationContext.getHashKeySerializationPair().write(key);
    }

    private Map.Entry<HK, HV> deserializeHashEntry(Map.Entry<ByteBuffer, ByteBuffer> source) {
        return Collections.singletonMap(readHashKey(source.getKey()), readHashValue(source.getValue())).entrySet()
                .iterator().next();
    }

    private HK readHashKey(ByteBuffer value) {
        return (HK) serializationContext.getHashKeySerializationPair().read(value);
    }

    private ByteBuffer rawHashValue(Object key) {
        return serializationContext.getHashValueSerializationPair().write(key);
    }

    /**
     * @return key 序列化
     */
    private ByteBuffer rawKey(H key) {
        return serializationContext.getKeySerializationPair().write(key);
    }

    private HV readHashValue(ByteBuffer value) {
        return (HV) (value == null ? value : serializationContext.getHashValueSerializationPair().read(value));
    }

    private Object readObject(ByteBuffer value) {
        return (Object) (value == null ? value : serializationContext.getHashValueSerializationPair().read(value));
    }

    private String readString(ByteBuffer value) {
        return (String) (value == null ? value : serializationContext.getHashValueSerializationPair().read(value));
    }

    private List<HV> deserializeHashValues(List<ByteBuffer> source) {
        List<HV> values = new ArrayList<>(source.size());
        for (ByteBuffer byteBuffer : source) {
            values.add(readHashValue(byteBuffer));
        }
        return values;
    }

    private List<Object> deserializeObjects(List<ByteBuffer> source) {
        List<Object> values = new ArrayList<>(source.size());
        for (ByteBuffer byteBuffer : source) {
            values.add(readObject(byteBuffer));
        }
        return values;
    }

    /**
     * @param function 指令方法
     * @param <T>      类型
     * @return 创建Mono
     */
    private <T> Mono<T> createMono(Function<ReactiveHashCommands, Publisher<T>> function) {
        Assert.notNull(function, "Function must not be null!");
        return template.createMono(connection -> function.apply(connection.hashCommands()));
    }

    private <T> Flux<T> createFlux(Function<ReactiveHashCommands, Publisher<T>> function) {
        Assert.notNull(function, "Function must not be null!");
        return template.createFlux(connection -> function.apply(connection.hashCommands()));
    }

    @Override
    public Mono<Boolean> putClass(H key, HV value) {
        Map<String, Object> map = Maps.newHashMap();
        try {
            Class<?> clazz = value.getClass();
            Field[] declaredFields = clazz.getDeclaredFields();
            String hash = getHashKeyPre(value);
            for (Field field : declaredFields) {
                field.setAccessible(true);
                map.put(hash + ":" + field.getName(), field.get(value));
            }
            map.put(CLASS, clazz.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createMono(connection -> Flux.fromIterable(() -> map.entrySet().iterator())
                .collectMap(entry -> rawHashKey(entry.getKey()), entry -> rawHashValue(entry.getValue()))
                .flatMap(serialized -> connection.hMSet(rawKey(key), serialized)));
    }

    @Override
    public Mono<HV> getClass(H key, Object hashKey) {
        return this.getClass(key, hashKey, null);
    }

    @Override
    public Mono<HV> getClass(H key, Object hashKey, Class<HV> clazz) {
        return this.getClassByName(key, clazz)
                .flatMap(clazzNow -> {
                    List<String> keys = Lists.newArrayList();
                    List<String> primaryKey = Lists.newArrayList();
                    Field[] declaredFields = clazzNow.getDeclaredFields();
                    for (Field field : declaredFields) {
                        if (field.getAnnotation(RedisPrimaryKey.class) != null) {
                            primaryKey.add(field.getName());
                        }
                        field.setAccessible(true);
                        keys.add(hashKey + ":" + field.getName());
                    }
                    Assert.isTrue(primaryKey.size() > 1, "该方法只适用于单个主键");
                    return createMono(connection -> Flux.fromIterable(keys)
                            .map(this::rawHashKey)
                            .collectList()
                            .flatMap(hks -> connection.hMGet(rawKey(key), hks)
                                    .map(this::deserializeObjects)))
                            .map(values -> {
                                try {
                                    HV hv = clazzNow.newInstance();
                                    int i = 0;
                                    for (Field field : declaredFields) {
                                        field.setAccessible(true);
                                        field.set(hv, values.get(i));
                                        i++;
                                    }
                                    return hv;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            });
                });
    }

    /**
     * @param key   redis key
     * @param clazz 类型
     * @return 根据名称获取类型
     */
    private Mono<Class<HV>> getClassByName(H key, Class<HV> clazz) {
        return Mono.justOrEmpty(clazz)
                .switchIfEmpty(createMono(connection -> connection.hGet(rawKey(key), rawHashKey(CLASS))
                        .map(this::readString)
                        .flatMap(className -> {
                            try {
                                return Mono.justOrEmpty((Class<HV>) Class.forName(className));
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                            return Mono.error(new RuntimeException("clazz 不存在"));
                        })));
    }

    /**
     * 获取 hashkey 前缀
     */
    private String getHashKeyPre(HV value) throws IllegalAccessException {
        Map<String, Object> keyMap = Maps.newHashMap();
        Class<?> clazz = value.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getAnnotation(RedisPrimaryKey.class) != null) {
                field.setAccessible(true);
                keyMap.put(field.getName(), field.get(value));
            }
        }
        Assert.notEmpty(keyMap, "Redis 对象不能没有 @RedisPrimaryKey 主键 ");
        return keyMap.entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .map(Object::toString)
                .collect(Collectors.joining(":"));

    }
}
