package com.uetty.rule.config.redis.operations;

import reactor.core.publisher.Mono;

public interface ReactiveClassOperations<H, HK, HV> {

    /**
     * @param key   redis key
     * @param value 对象信息
     * @return 存储redis 主键:属性  值的形式
     */
    Mono<Boolean> putClass(H key, HV value);

    /**
     * @param key     redis key
     * @param hashKey 主键值
     * @return 获取对象（适用于单个主键）
     */
    Mono<HV> getClass(H key, Object hashKey);

}
