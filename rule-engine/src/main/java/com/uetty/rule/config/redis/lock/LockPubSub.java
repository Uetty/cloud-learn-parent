package com.uetty.rule.config.redis.lock;


/**
 * 锁的发布订阅
 */
public class LockPubSub {

    public static final Long unlockMessage = 0L;

    protected LockEntry createEntry(LockEntry lockEntry) {
        return new LockEntry(lockEntry);
    }

    protected void onMessage(LockEntry value, Long message) {
        //判断收到的消息;
        if (message.equals(unlockMessage)) {
            //获得许可证，将其返还给信号量。
            value.getLatch().release();
            while (true) {
                Runnable runnableToExecute = null;
                synchronized (value) {
                    //从监听队列里取出一个任务
                    Runnable runnable = value.getListeners().poll();
                    //如果存在任务
                    if (runnable != null) {
                        //尝试获取锁
                        if (value.getLatch().tryAcquire()) {
                            //设置任务
                            runnableToExecute = runnable;
                        } else {
                            //没有获取到锁,将任务重新添加回监听队列
                            value.addListener(runnable);
                        }
                    }

                    //runnableToExecute,不为空代表获取到锁,执行线程
                    if (runnableToExecute != null) {
                        runnableToExecute.run();
                    } else {
                        return;
                    }
                }
            }
        }
    }
}
