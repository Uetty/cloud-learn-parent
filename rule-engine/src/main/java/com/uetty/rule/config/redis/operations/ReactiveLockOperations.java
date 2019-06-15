package com.uetty.rule.config.redis.operations;

import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

/**
 * redis lock
 */
public interface ReactiveLockOperations {


    /**
     * 尝试获取锁
     *
     * @param key key名
     * @return true:代表被锁  false:代表没被锁
     */
    Mono<Boolean> tryLock(String key);

    /**
     * 尝试获取锁
     *
     * @param waitTime  等待时间
     * @param leaseTime 超时时间
     * @param unit      单位
     * @return true:代表被锁  false:代表没被锁
     */
    boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * @param key key名
     * @return 上锁
     */
    Mono<Void> lock(String key);

    boolean isLocked();

}
