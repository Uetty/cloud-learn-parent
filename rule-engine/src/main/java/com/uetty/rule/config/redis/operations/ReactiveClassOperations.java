package com.uetty.rule.config.redis.operations;

import com.uetty.rule.utils.FunctionCollection;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@SuppressWarnings({"unchecked", "varargs"})
public interface ReactiveClassOperations<H, HK, HV> {

    /**
     * @param values 对象信息
     * @return 存储redis 主键:属性  值的形式
     */
    Mono<Boolean> putClass(HV... values);

    /**
     * @param values 对象信息
     * @return 存储redis 主键:属性  值的形式
     */
    Mono<Boolean> putClass(Collection<HV> values);

    /**
     * @param key    redis key
     * @param values 对象信息
     * @return 存储redis 主键:属性  值的形式
     */
    Mono<Boolean> putClass(H key, HV... values);

    /**
     * @param key    redis key
     * @param values 对象信息
     * @return 存储redis 主键:属性  值的形式
     */
    Mono<Boolean> putClass(H key, Collection<HV> values);


    /**
     * @param key     redis key
     * @param hashKey 主键值
     * @return 获取对象（适用于单个主键）
     */
    Mono<HV> getClass(H key, FunctionCollection columns, Object hashKey);

    /**
     * @param key     redis key
     * @param hashKey 主键值
     * @return 获取对象（适用于单个主键）
     */
    default Mono<HV> getClass(H key, Object hashKey) {
        return getClass(key, FunctionCollection.create(), hashKey);
    }

    /**
     * @param key     redis key
     * @param hashKey 主键值
     * @return 获取对象（适用于单个主键）
     */
    Mono<List<HV>> getClass(H key, FunctionCollection columns, Collection<HV> hashKey);

    /**
     * @param key     redis key
     * @param hashKey 主键值
     * @return 获取对象（适用于单个主键）
     */
    default Mono<List<HV>> getClass(H key, Collection<HV> hashKey) {
        return getClass(key, FunctionCollection.create(), hashKey);
    }

    ;

}
