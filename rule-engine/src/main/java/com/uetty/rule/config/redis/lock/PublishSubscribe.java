package com.uetty.rule.config.redis.lock;

import com.google.common.collect.Maps;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentMap;

public abstract class PublishSubscribe<E extends PubSubEntry<E>> {

    private final ConcurrentMap<String, E> entries = Maps.newConcurrentMap();

    public E getEntry(String entryName) {
        return entries.get(entryName);
    }

    protected abstract Mono<E> createEntry(E newPromise);

    /**
     * 消息处理
     *
     * @param value
     * @param message
     */
    protected abstract void onMessage(E value, Long message);

}
