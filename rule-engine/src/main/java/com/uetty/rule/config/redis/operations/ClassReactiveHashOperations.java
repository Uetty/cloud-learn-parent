package com.uetty.rule.config.redis.operations;

import org.springframework.data.redis.core.ReactiveHashOperations;
import reactor.core.publisher.Mono;

public interface ClassReactiveHashOperations<H, HK, HV> extends ReactiveHashOperations<H, HK, HV> {

    /**
     * @param key redis key
     * @param value 对象信息
     * @return 存储redis 主键:属性  值的形式
     */
    Mono<Boolean> putClass(H key, HV value) throws IllegalAccessException;

}
