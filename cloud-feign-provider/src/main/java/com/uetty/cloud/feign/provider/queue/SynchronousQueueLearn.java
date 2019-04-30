package com.uetty.cloud.feign.provider.queue;

import java.util.concurrent.SynchronousQueue;

import static rx.internal.util.unsafe.UnsafeAccess.UNSAFE;

/**
 * 1.对于每一个take的线程会阻塞直到有一个put的线程放入元素为止
 * 2.peek操作或者迭代器操作也是无效的(peek返回null，迭代器返回空迭代器)
 *
 * https://blog.csdn.net/u011518120/article/details/53906484（没看懂下次再补）
 */
public class SynchronousQueueLearn {

    private static SynchronousQueue queue = new SynchronousQueue();

    /**
     * 用于旋转控制的CPU数量
     */
    static final int NCPUS = Runtime.getRuntime().availableProcessors();

    /**
     * 在定时等待中阻塞之前旋转的次数
     */
    static final int maxTimedSpins = (NCPUS < 2) ? 0 : 32;
    /**
     * 在不定时等待中阻塞之前旋转的次数。
     */
    static final int maxUntimedSpins = maxTimedSpins * 16;
    /**
     * 旋转速度更快的纳秒数
     */
    static final long spinForTimeoutThreshold = 1000L;

    private static final long headOffset;
    /**
     * 内存操作类
     */
    private static final sun.misc.Unsafe UNSAFE;

    static {
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class k = SynchronousQueueLearn.class;
            headOffset = UNSAFE.objectFieldOffset
                    (k.getDeclaredField("head"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }


    /**
     * transfer方法，这个方法执行put或者take操作。
     */
    private void hookTransfer() throws InterruptedException {
        //1.transfer分两种,队列（TransferQueue）和栈（TransferStack）
        queue.take();
    }

    /**
     * TransferStack有三个状态:
     * 1:request，就是获取队列的请求对应于take操作  0
     * 2:入data的操作对应于put  1
     * 3:完成了匹配操的fulfill对应于一个交付操作  2
     */
    private void hookTransferStack() {
        final int REQUEST = 0;
        final int DATA = 1;
        final int FULFILLING = 2;
    }
}
