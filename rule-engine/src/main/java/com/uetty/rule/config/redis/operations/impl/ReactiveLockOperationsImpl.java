package com.uetty.rule.config.redis.operations.impl;

import com.google.common.collect.Lists;
import com.uetty.rule.config.redis.operations.ReactiveLockOperations;
import com.uetty.rule.config.redis.script.ScriptConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * redis lock
 */
@RequiredArgsConstructor
public class ReactiveLockOperationsImpl implements ReactiveLockOperations {

    private final @NonNull ReactiveRedisTemplate<?, ?> template;

    private static final long LOCK_EXPIRATION_INTERVAL_SECONDS = 30;
    //初始过期时间
    protected long internalLockLeaseTime = TimeUnit.SECONDS.toMillis(LOCK_EXPIRATION_INTERVAL_SECONDS);
    final UUID id;

    public void lock(String key) {
        try {
            lockInterruptibly(key);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void lockInterruptibly(String key) throws InterruptedException {
        lockInterruptibly(key, -1, null);
    }

    public Mono<Boolean> tryLock(String key) {
        return tryLockAsync(key);
    }

    private Mono<Boolean> tryLockAsync(String key) {
        return tryLockAsync(key, Thread.currentThread().getId());
    }

    private Mono<Boolean> tryLockAsync(String key, long threadId) {
        return tryAcquireOnceAsync(key, -1, null, threadId);
    }

    private Mono<Boolean> tryAcquireOnceAsync(String key, long leaseTime, TimeUnit unit, long threadId) {
        if (leaseTime != -1) {
            return tryLockInnerAsync(key, leaseTime, unit, threadId);
        }
        return null;
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public void unlock() {

    }

    public Condition newCondition() {
        return null;
    }

    @Override
    public void lockInterruptibly(String key, long leaseTime, TimeUnit unit) throws InterruptedException {
        long threadId = Thread.currentThread().getId();
        //尝试获取锁
        tryAcquire(key, leaseTime, unit, threadId)
                .map(ttl -> {
                    //判断过期时间
                    if (ttl != null) {
                        //订阅该线程，通知已经该线程已经获得锁
                        subscribe(key, threadId);
                    }
                    return null;
                });
    }


    /**
     * 订阅
     *
     * @param key      reids key
     * @param threadId 线程id
     */
    private void subscribe(String key, long threadId) {
        template.listenTo(ChannelTopic.of(getChannelName(key)));
    }

    private Mono<Long> tryAcquire(String key, long leaseTime, TimeUnit unit, long threadId) {
        return tryAcquireAsync(key, leaseTime, unit, threadId);
    }

    /**
     * @param key       锁名
     * @param leaseTime 等待时间
     * @param unit      时间单位
     * @param threadId  线程id
     * @return 尝试获取锁
     */
    private Mono<Long> tryAcquireAsync(String key, long leaseTime, TimeUnit unit, long threadId) {
        if (leaseTime != -1) {
            //设置了等待时间
            return tryLockInnerAsync(key, leaseTime, unit, threadId);
        }
        //默认等待时间
        return tryLockInnerAsync(key, LOCK_EXPIRATION_INTERVAL_SECONDS, TimeUnit.SECONDS, threadId);
    }

    @SuppressWarnings("unchecked")
    private <T> Mono<T> tryLockInnerAsync(String key, long leaseTime, TimeUnit unit, long threadId) {
        internalLockLeaseTime = unit.toMillis(leaseTime);
        //redis key(锁名)
        List<String> keys = Lists.newArrayList();
        keys.add(key);
        List<Object> params = Lists.newArrayList();
        //初始过期时间
        params.add(internalLockLeaseTime);
        //hash key（uuid+threadid）
        params.add(getLockName(threadId));
        ReactiveRedisTemplate<String, Object> template = (ReactiveRedisTemplate<String, Object>) this.template;
        return template.execute(ScriptConfig.<T>getScript(ScriptConfig.ScriptType.LOCK), keys, params).last();
    }

    @Override
    public boolean tryLock(long var1, long var3, TimeUnit var5) throws InterruptedException {
        return false;
    }

    @Override
    public void lock(long var1, TimeUnit var3) {

    }

    @Override
    public void forceUnlock() {

    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public boolean isHeldByCurrentThread() {
        return false;
    }

    @Override
    public int getHoldCount() {
        return 0;
    }


    protected String getEntryName(String keyName) {
        return id + ":" + keyName;
    }

    private String getChannelName(String key) {
        return prefixName("redis_lock_topic", key);
    }

    protected String prefixName(String prefix, String name) {
        if (name.contains("{")) {
            return prefix + ":" + name;
        }
        return prefix + ":{" + name + "}";
    }

    private String getLockName(long threadId) {
        return id + ":" + threadId;
    }

    public static void main(String[] args) {
        RedissonClient client = Redisson.create();
        RLock lock = client.getLock("aa");


    }
}
