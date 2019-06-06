package com.uetty.rule.config.redis.lock;

import reactor.core.publisher.Mono;

public interface PubSubEntry<E> {

    void aquire();

    int release();

    Mono<E> getPromise();

}
