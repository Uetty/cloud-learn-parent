package com.uetty.rule.config.redis.operations;

import java.util.concurrent.TimeUnit;

/**
 * redis lock
 */
public interface ReactiveLockOperations{

    void lockInterruptibly(String key,long leaseTime, TimeUnit var3) throws InterruptedException;

    boolean tryLock(long var1, long var3, TimeUnit var5) throws InterruptedException;

    void lock(long var1, TimeUnit var3);

    void forceUnlock();

    boolean isLocked();

    boolean isHeldByCurrentThread();

    int getHoldCount();
}
