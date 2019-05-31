package com.uetty.rule.config.redis.operations.impl;

import com.uetty.rule.config.redis.operations.ReactiveLockOperations;
import com.uetty.rule.config.redis.script.ScriptConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * redis lock
 */
@RequiredArgsConstructor
public class ReactiveLockOperationsImpl implements ReactiveLockOperations {

    private final @NonNull ReactiveRedisTemplate<?, ?> template;

    public static final long LOCK_EXPIRATION_INTERVAL_SECONDS = 30;

    @Override
    public void lock() {
        try {
            lockInterruptibly();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lockInterruptibly(-1, null);
    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {

    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Override
    public void lockInterruptibly(long var1, TimeUnit var3) throws InterruptedException {
        long threadId = Thread.currentThread().getId();
    }

    private Mono<Long> tryAcquire(long leaseTime, TimeUnit unit, long threadId) {
        if (leaseTime != -1) {
            //不等待，直接执行
            return tryLockInnerAsync(leaseTime, unit, threadId);
        }
        return tryLockInnerAsync(LOCK_EXPIRATION_INTERVAL_SECONDS, TimeUnit.SECONDS, threadId);
    }

    private Mono<Long> tryLockInnerAsync(long leaseTime, TimeUnit unit, long threadId) {
        return template.execute(ScriptConfig.<Long>getScript(ScriptConfig.ScriptType.LOCK)).last();
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

    public static void main(String[] args) {
        RedissonClient client = Redisson.create();
        RLock lock = client.getLock("aa");


    }
}
