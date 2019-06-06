package com.uetty.rule.config.redis.lock;

import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class LockEntry implements PubSubEntry<LockEntry> {

    /**
     * 计数器
     */
    private int counter;

    private final Mono<LockEntry> promise;

    private final Semaphore latch;

    /**
     * 监听队列
     */
    private final ConcurrentLinkedQueue<Runnable> listeners = new ConcurrentLinkedQueue<>();

    public LockEntry(LockEntry promise) {
        counter = 0;
        this.promise = Mono.just(promise);
        this.latch = new Semaphore(0);
    }

    @Override
    public void aquire() {
        counter++;
    }

    @Override
    public int release() {
        return --counter;
    }

    @Override
    public Mono<LockEntry> getPromise() {
        return null;
    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    public boolean removeListener(Runnable listener) {
        return listeners.remove(listener);
    }

    public ConcurrentLinkedQueue<Runnable> getListeners() {
        return listeners;
    }

    public Semaphore getLatch() {
        return latch;
    }
}
