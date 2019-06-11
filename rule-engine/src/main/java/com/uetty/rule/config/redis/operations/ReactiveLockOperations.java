package com.uetty.rule.config.redis.operations;

import reactor.core.publisher.Mono;

/**
 * redis lock
 */
public interface ReactiveLockOperations {


    Mono<Boolean> tryLock(String key);

    boolean isLocked();

}
