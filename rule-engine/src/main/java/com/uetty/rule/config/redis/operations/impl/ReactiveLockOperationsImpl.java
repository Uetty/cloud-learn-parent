package com.uetty.rule.config.redis.operations.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.uetty.cloud.utils.Convert;
import com.uetty.rule.config.redis.lock.LockPubSub;
import com.uetty.rule.config.redis.operations.ReactiveLockOperations;
import com.uetty.rule.config.redis.script.ScriptConfig;
import com.uetty.rule.config.redis.template.ClassReactiveRedisTemplate;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.data.redis.connection.ReactiveScriptingCommands;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.function.Function;

/**
 * redis lock(重入锁)
 */
@RequiredArgsConstructor
public class ReactiveLockOperationsImpl implements ReactiveLockOperations {

    private final @NonNull ClassReactiveRedisTemplate<?, ?> template;
    private final @NonNull RedisSerializationContext<?, ?> serializationContext;

    private static final ConcurrentMap<String, Timeout> expirationRenewalMap = Maps.newConcurrentMap();
    //默认过期时间
    private static final long LOCK_EXPIRATION_INTERVAL_SECONDS = 30;
    //初始过期时间
    protected long internalLockLeaseTime = TimeUnit.SECONDS.toMillis(LOCK_EXPIRATION_INTERVAL_SECONDS);
    final UUID id;

    private ByteBuffer rawKey(Object key) {
        return serializationContext.getHashValueSerializationPair().write(key);
    }

    protected ByteBuffer scriptBytes(String key) {
        return serializationContext.getStringSerializationPair().getWriter().write(key);
    }

    private <T> Flux<T> createFlux(Function<ReactiveScriptingCommands, Publisher<T>> function) {
        Assert.notNull(function, "Function must not be null!");
        return template.createFlux(connection -> function.apply(connection.scriptingCommands()));
    }

    private <T> Mono<T> createMono(Function<ReactiveScriptingCommands, Publisher<T>> function) {
        Assert.notNull(function, "Function must not be null!");
        return template.createMono(connection -> function.apply(connection.scriptingCommands()));
    }


    public Mono<Void> lock(String key) {
        try {
            return lockInterruptibly(key);
        } catch (InterruptedException e) {
            return Mono.error(e);
        }
    }

    public Mono<Void> lockInterruptibly(String key) throws InterruptedException {
        return lockInterruptibly(key, -1, null);
    }

    public Mono<Void> lockInterruptibly(String key, long leaseTime, TimeUnit unit) throws InterruptedException {
        long threadId = Thread.currentThread().getId();
        AtomicReference<Long> tta = new AtomicReference<>();
        //尝试获取锁
        return tryAcquire(key, leaseTime, unit, threadId)
                .filter(Objects::nonNull)//过期时间为空，则代表获取到锁。
                .flatMapMany(time -> this.subscribe(key))//没获取到锁，订阅该key，等待其他线程释放锁，其他线程释放锁的时候发布
                .map(v -> tryAcquire(key, leaseTime, unit, threadId)//上锁后再获取一次锁
                        .flatMap(ttl -> { //监听订阅，获取发布的信息
                            tta.set(ttl);
                            if (ttl >= 0) {
                                return getEntry(key).flatMap(message -> tryAcquire(key, ttl, TimeUnit.MILLISECONDS, threadId));
                            } else {
                                return getEntry(key).flatMap(message -> tryAcquire(key, 0L, TimeUnit.MILLISECONDS, threadId));
                            }
                        })
                        .repeat(() -> tta.get() != null)//循环，直到ttl为null
                        .subscribe()
                )
                .then(this.unsubscribe(key));//取消订阅
    }

    @Override
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
            return tryLockInnerAsync(key, leaseTime, unit, threadId).map(Convert::toBool);
        }
        return tryLockInnerAsync(key, LOCK_EXPIRATION_INTERVAL_SECONDS, TimeUnit.SECONDS, threadId).map(Convert::toBool);
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public Mono<Void> unlock(String key) {
        return unlockInnerAsync(key, Thread.currentThread().getId())
                .doOnSuccess(opStatus -> {
                    if (opStatus == null) {
                        throw new IllegalMonitorStateException("attempt to unlock lock, not locked by current thread by node id: "
                                + id + " thread-id: " + Thread.currentThread().getId());
                    }
                    if (opStatus) {
                        cancelExpirationRenewal(key);
                    }
                })
                .then();
    }

    private void cancelExpirationRenewal(String key) {
        Timeout task = expirationRenewalMap.remove(getEntryName(key));
        if (task != null) {
            task.cancel();
        }
    }

    @SuppressWarnings("unchecked")
    private Mono<Boolean> unlockInnerAsync(String key, long threadId) {
        ReactiveRedisTemplate<String, Object> template = (ReactiveRedisTemplate<String, Object>) this.template;
        return template.execute(ScriptConfig.<Boolean>getScript(ScriptConfig.ScriptType.UN_LOCK),
                Lists.newArrayList(key, getChannelName(key)),
                Lists.newArrayList(LockPubSub.unlockMessage, internalLockLeaseTime, getLockName(threadId)))
                .next();
    }

    public Condition newCondition() {
        return null;
    }


    private Mono<? extends ReactiveSubscription.Message<String, ?>> getEntry(String key) {
        return template.listenToChannel(getEntryName(key)).next();
    }


    /**
     * 订阅
     *
     * @param key reids key
     */
    private Mono<Void> subscribe(String key) {
        return template.execute(a -> a.pubSubCommands().subscribe(rawKey(key))).next();
    }

    /**
     * @param key reids key
     * @return 取消订阅
     */
    private Mono<Void> unsubscribe(String key) {
        return template.execute(a -> a.pubSubCommands().createSubscription().flatMap(sub -> sub.unsubscribe(rawKey(key)))).next();
    }

    /**
     * @param key       锁名
     * @param leaseTime 过期时间
     * @param unit      时间单位
     * @param threadId  线程id
     * @return 申请锁并返回锁有效期还剩余的时间（如果为空说明锁未被其它线程申请直接获取并返回，如果获取到时间，则进入等待竞争逻辑）
     */
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
        Mono<Long> mono = tryLockInnerAsync(key, LOCK_EXPIRATION_INTERVAL_SECONDS, TimeUnit.SECONDS, threadId);
        return mono.doOnSuccess(ttl -> {
            if (ttl == null) {
                //过期时间不为空，进入竞争锁状态
                scheduleExpirationRenewal(key, threadId);
            }
        });
    }

    private Mono<Long> tryLockInnerAsync(String key, long leaseTime, TimeUnit unit, long threadId) {
        internalLockLeaseTime = unit.toMillis(leaseTime);
        DefaultRedisScript<Long> redisScript = ScriptConfig.getScript(ScriptConfig.ScriptType.LOCK);
        return createFlux(commands -> commands.<Long>eval(scriptBytes(redisScript.getScriptAsString()), ReturnType.INTEGER, 1,
                scriptBytes(key), rawKey((int) internalLockLeaseTime), scriptBytes(getLockName(threadId)))).next();
    }

    /**
     * 过期时间不为空，进入竞争锁状态
     *
     * @param key      锁名
     * @param threadId 线程id
     */
    @SuppressWarnings("unchecked")
    private void scheduleExpirationRenewal(String key, long threadId) {
        if (expirationRenewalMap.containsKey(getEntryName(key))) {
            return;
        }
        ReactiveRedisTemplate<String, Object> template = (ReactiveRedisTemplate<String, Object>) this.template;
        HashedWheelTimer hashedWheelTimer = new HashedWheelTimer();
        Timeout task = hashedWheelTimer.newTimeout(timeout -> template.execute(ScriptConfig.<Boolean>getScript(ScriptConfig.ScriptType.SCHEDULE_LOCK),
                Lists.newArrayList(key),
                Lists.newArrayList(internalLockLeaseTime, getLockName(threadId)))
                .next()
                .doOnSuccess(ret -> {
                    expirationRenewalMap.remove(getEntryName(key));
                    if (ret) {
                        scheduleExpirationRenewal(key, threadId);
                    }
                })
                .subscribe(), internalLockLeaseTime / 3, TimeUnit.MILLISECONDS);


        if (expirationRenewalMap.putIfAbsent(getEntryName(key), task) != null) {
            task.cancel();
        }
    }

    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) {
        long time = unit.toMillis(waitTime);
        long current = System.currentTimeMillis();
        final long threadId = Thread.currentThread().getId();
        Long ttl = tryAcquire(key, leaseTime, unit, threadId).toFuture().join();
        // lock acquired
        if (ttl == null) {
            return true;
        }
        time -= (System.currentTimeMillis() - current);
        if (time <= 0) {
            acquireFailed(threadId);
            return false;
        }
        current = System.currentTimeMillis();
        subscribe(key);
        return false;
    }

    private void acquireFailed(long threadId) {

    }

    public void lock(long var1, TimeUnit var3) {

    }

    public void forceUnlock() {

    }

    public boolean isLocked() {
        return false;
    }

    public boolean isHeldByCurrentThread() {
        return false;
    }

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

}
