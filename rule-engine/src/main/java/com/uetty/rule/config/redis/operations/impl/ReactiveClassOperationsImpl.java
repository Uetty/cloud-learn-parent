package com.uetty.rule.config.redis.operations.impl;

import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.SerializationUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.uetty.rule.config.redis.annotation.RedisPrimaryKey;
import com.uetty.rule.config.redis.operations.ReactiveClassOperations;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.reflection.ReflectionException;
import org.apache.logging.log4j.util.Strings;
import org.reactivestreams.Publisher;
import org.springframework.data.redis.connection.ReactiveHashCommands;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class ReactiveClassOperationsImpl<H, HK, HV> implements ReactiveClassOperations<H, HK, HV> {

    private final @NonNull ReactiveRedisTemplate<?, ?> template;
    private final @NonNull RedisSerializationContext<H, ?> serializationContext;

    private final @NonNull RedisSerializationContext<Object, ?> serializationString = RedisSerializationContext.java();

    private final static String CLASS = "@class";

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
    public Mono<HV> getClass(H key, Object hashKey, SFunction<HV, ?>... columns) {
        return null;
    }

    @Override
    public Mono<HV> getClass(H key, Object hashKey) {
        Assert.notNull(hashKey, "hashKey must not be null!");
        try {
            HV hv = (HV) hashKey;
            return this.getClassDetail(key, hashKey, (Class<HV>) hv.getClass());
        } catch (ClassCastException e) {
            //强转错误
        }
        return this.getClassDetail(key, hashKey, null);
    }

    private Mono<HV> getClassDetail(H key, Object hashKey, Class<HV> clazz) {
        return this.getClassByName(key, clazz)
                .map(clazzNow -> {
                    boolean ret = clazz != null;
                    List<String> keys = Lists.newArrayList();
                    String preKey = "";
                    ClassField<HV> classField = getClassField(clazzNow, field -> keys.add(findHashKey(field, hashKey, preKey, ret)));
                    if (!ret) {
                        Assert.isTrue(classField.getPrimaryKey().size() == 1, "该方法只适用于单个主键");
                    }
                    classField.setKeys(keys);
                    return classField;
                })
                .flatMap(classField -> createMono(connection -> Flux.fromIterable(classField.getKeys())
                        .map(this::rawHashKey)
                        .collectList()
                        .flatMap(hks -> connection.hMGet(rawKey(key), hks)
                                .map(this::deserializeObjects))
                        .map(values -> toMap(classField.getKeys(), values))
                        .map(valueMap -> this.doFinally(classField, valueMap))));
    }

    /**
     * @param field   字段
     * @param hashKey 传入的值
     * @param preKey  前缀
     * @param ret     是否为对象
     * @return hashkey
     */
    private String findHashKey(Field field, Object hashKey, String preKey, boolean ret) {
        if (ret) {
            try {
                if (Strings.isNotEmpty(preKey)) {
                    preKey = getHashKeyPre((HV) hashKey);
                }
                return new StringJoiner(preKey).add(":").add(field.getName()).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return new StringJoiner(Objects.toString(hashKey)).add(":").add(field.getName()).toString();
    }

    /**
     * @return map 转成属性对象
     */
    private HV doFinally(ClassField<HV> classField, Map<String, Object> valueMap) {
        try {
            HV hv = classField.getClazz().getDeclaredConstructor().newInstance();
            for (Field field : classField.getDeclaredFields()) {
                field.setAccessible(true);
                field.set(hv, valueMap.get(field.getName()));
            }
            return hv;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param keys   redis Hash Key
     * @param values redis Hash Value
     * @return 组成 FieldName-value Map
     */
    private Map<String, Object> toMap(List<String> keys, List<Object> values) {
        Assert.isTrue(keys.size() == values.size(), "key和value数量不相等 ");
        Map<String, Object> map = Maps.newHashMap();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = values.get(i);
            String[] split = key.split(":");
            Assert.isTrue(split.length > 0, "分割属性出错 ");
            String fieldName = split[split.length - 1];
            map.put(fieldName, value);
        }
        return map;
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

    private <R> ClassField<HV> getClassField(Class<HV> clazz, Function<Field, R> function) {
        List<String> primaryKey = Lists.newArrayList();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getAnnotation(RedisPrimaryKey.class) != null) {
                primaryKey.add(field.getName());
            }
            field.setAccessible(true);
            function.apply(field);
        }
        return new ClassField<>(primaryKey, declaredFields, clazz);
    }

    private String methodToProperty(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else {
            if (!name.startsWith("get") && !name.startsWith("set")) {
                throw new ReflectionException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
            }
            name = name.substring(3);
        }
        if (name.length() == 1 || name.length() > 1 && !Character.isUpperCase(name.charAt(1))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }

        return name;
    }

    /**
     * 通过反序列化转换 lambda 表达式，该方法只能序列化 lambda 表达式，不能序列化接口实现或者正常非 lambda 写法的对象
     *
     * @param lambda lambda对象
     * @return 返回解析后的 SerializedLambda
     */
    public SerializedLambda resolve(SFunction<?, ?> lambda) {
        if (!lambda.getClass().isSynthetic()) {
            throw ExceptionUtils.mpe("该方法仅能传入 lambda 表达式产生的合成类");
        }
        try (ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(SerializationUtils.serialize(lambda))) {
            @Override
            protected Class<?> resolveClass(ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
                Class<?> clazz = super.resolveClass(objectStreamClass);
                return clazz == java.lang.invoke.SerializedLambda.class ? SerializedLambda.class : clazz;
            }
        }) {
            return (SerializedLambda) objIn.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw ExceptionUtils.mpe("This is impossible to happen", e);
        }
    }
}

/**
 * class存储对象
 */
@Data
class ClassField<HV> {

    private List<String> primaryKey;//主键列表

    private Field[] declaredFields;//属性数组

    private List<String> keys;//REDIS HASH KEY

    private Class<HV> clazz;

    public ClassField(List<String> primaryKey, Field[] declaredFields, Class<HV> clazz) {
        this.primaryKey = primaryKey;
        this.declaredFields = declaredFields;
        this.clazz = clazz;
    }
}


