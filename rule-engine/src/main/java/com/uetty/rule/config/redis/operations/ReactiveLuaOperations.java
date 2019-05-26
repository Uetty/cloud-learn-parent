package com.uetty.rule.config.redis.operations;

import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Lua 脚本操作
 */
public interface ReactiveLuaOperations<K, V> {

    /**
     * @param sortedSetKey 排序key
     * @param hashKey 哈希key
     * @param start 开始点
     * @param end 结束点
     * @return 根据排序key获取id，去hash key查询结果
     */
    Mono<List<V>> getHashFromSortedSet(K sortedSetKey, K hashKey, long start, long end);

}
